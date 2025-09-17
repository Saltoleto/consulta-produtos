package com.exemplo.processamento;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Processador de contas melhorado com:
 * - Configurações externalizáveis
 * - Métricas e observabilidade
 * - Circuit breaker pattern
 * - Retry policies
 * - Shutdown graceful
 * - Logs estruturados
 * - Validação de entrada
 * - Tratamento robusto de exceções
 */
@Service
public class ContaProcessorMelhorado implements HealthIndicator {

    private static final Logger logger = LoggerFactory.getLogger(ContaProcessorMelhorado.class);

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final MeterRegistry meterRegistry;
    private final ContaValidator contaValidator;
    private final CircuitBreakerService circuitBreakerService;

    // Configurações externalizáveis
    @Value("${app.processamento.lote-size:100}")
    private int loteSize;

    @Value("${app.processamento.max-lotes-simultaneos:30}")
    private int maxLotesSimultaneos;

    @Value("${app.processamento.max-kafka-simultaneos:50}")
    private int maxKafkaSimultaneos;

    @Value("${app.processamento.timeout-shutdown:30}")
    private int timeoutShutdownSegundos;

    @Value("${app.kafka.topic.contas-processadas:contas.processadas}")
    private String kafkaTopicContasProcessadas;

    @Value("${app.processamento.enable-circuit-breaker:true}")
    private boolean enableCircuitBreaker;

    // Executors com nomes descritivos
    private ExecutorService persistExecutor;
    private ExecutorService kafkaExecutor;

    // Semáforos para controle de concorrência
    private Semaphore lotesSemaphore;
    private Semaphore kafkaSemaphore;

    // Métricas
    private Counter lotesProcessadosCounter;
    private Counter contasProcessadasCounter;
    private Counter errosProcessamentoCounter;
    private Counter eventosKafkaEnviadosCounter;
    private Counter eventosKafkaFalhasCounter;
    private Timer tempoProcessamentoLoteTimer;
    private Timer tempoEnvioKafkaTimer;
    private AtomicInteger lotesSemaphoreUtilizacao;
    private AtomicInteger kafkaSemaphoreUtilizacao;

    // Estado para health check
    private volatile boolean healthy = true;
    private volatile String lastError = null;
    private volatile Instant lastProcessingTime = Instant.now();

    public ContaProcessorMelhorado(NamedParameterJdbcTemplate jdbcTemplate,
                                   KafkaTemplate<String, String> kafkaTemplate,
                                   MeterRegistry meterRegistry,
                                   ContaValidator contaValidator,
                                   CircuitBreakerService circuitBreakerService) {
        this.jdbcTemplate = jdbcTemplate;
        this.kafkaTemplate = kafkaTemplate;
        this.meterRegistry = meterRegistry;
        this.contaValidator = contaValidator;
        this.circuitBreakerService = circuitBreakerService;
    }

    @PostConstruct
    public void inicializar() {
        logger.info("Inicializando ContaProcessor com configurações: loteSize={}, maxLotesSimultaneos={}, maxKafkaSimultaneos={}",
                loteSize, maxLotesSimultaneos, maxKafkaSimultaneos);

        // Inicializar executors com thread factories nomeadas
        this.persistExecutor = Executors.newVirtualThreadPerTaskExecutor();
        this.kafkaExecutor = Executors.newVirtualThreadPerTaskExecutor();

        // Inicializar semáforos
        this.lotesSemaphore = new Semaphore(maxLotesSimultaneos);
        this.kafkaSemaphore = new Semaphore(maxKafkaSimultaneos);

        // Inicializar métricas
        inicializarMetricas();

        logger.info("ContaProcessor inicializado com sucesso");
    }

    private void inicializarMetricas() {
        this.lotesProcessadosCounter = Counter.builder("conta_processor_lotes_processados_total")
                .description("Total de lotes processados")
                .register(meterRegistry);

        this.contasProcessadasCounter = Counter.builder("conta_processor_contas_processadas_total")
                .description("Total de contas processadas")
                .register(meterRegistry);

        this.errosProcessamentoCounter = Counter.builder("conta_processor_erros_total")
                .description("Total de erros no processamento")
                .tag("tipo", "processamento")
                .register(meterRegistry);

        this.eventosKafkaEnviadosCounter = Counter.builder("conta_processor_kafka_eventos_enviados_total")
                .description("Total de eventos enviados para Kafka")
                .register(meterRegistry);

        this.eventosKafkaFalhasCounter = Counter.builder("conta_processor_kafka_falhas_total")
                .description("Total de falhas no envio para Kafka")
                .register(meterRegistry);

        this.tempoProcessamentoLoteTimer = Timer.builder("conta_processor_lote_processamento_duration")
                .description("Tempo de processamento de lote")
                .register(meterRegistry);

        this.tempoEnvioKafkaTimer = Timer.builder("conta_processor_kafka_envio_duration")
                .description("Tempo de envio para Kafka")
                .register(meterRegistry);

        // Gauges para monitorar utilização dos semáforos
        this.lotesSemaphoreUtilizacao = new AtomicInteger(0);
        this.kafkaSemaphoreUtilizacao = new AtomicInteger(0);

        Gauge.builder("conta_processor_lotes_semaphore_utilizacao")
                .description("Utilização atual do semáforo de lotes")
                .register(meterRegistry, this, processor -> processor.lotesSemaphoreUtilizacao.get());

        Gauge.builder("conta_processor_kafka_semaphore_utilizacao")
                .description("Utilização atual do semáforo de Kafka")
                .register(meterRegistry, this, processor -> processor.kafkaSemaphoreUtilizacao.get());

        Gauge.builder("conta_processor_lotes_semaphore_disponivel")
                .description("Permits disponíveis no semáforo de lotes")
                .register(meterRegistry, this, processor -> processor.lotesSemaphore.availablePermits());

        Gauge.builder("conta_processor_kafka_semaphore_disponivel")
                .description("Permits disponíveis no semáforo de Kafka")
                .register(meterRegistry, this, processor -> processor.kafkaSemaphore.availablePermits());
    }

    /**
     * Método principal para processar lista de contas
     */
    public void processarLotes(List<Conta> contas) {
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);
        
        try {
            logger.info("Iniciando processamento de {} contas em lotes de {}", contas.size(), loteSize);
            
            // Validação de entrada
            if (contas == null || contas.isEmpty()) {
                logger.warn("Lista de contas vazia ou nula recebida");
                return;
            }

            // Validar contas
            List<Conta> contasValidas = contaValidator.validarEFiltrarContas(contas);
            if (contasValidas.size() != contas.size()) {
                logger.warn("Filtradas {} contas inválidas de um total de {}", 
                    contas.size() - contasValidas.size(), contas.size());
            }

            // Dividir em lotes
            List<List<Conta>> lotes = ListUtils.partition(contasValidas, loteSize);
            logger.info("Dividindo {} contas válidas em {} lotes", contasValidas.size(), lotes.size());

            // Processar lotes
            CompletableFuture<?>[] futures = lotes.stream()
                    .map(this::processarLoteAsync)
                    .toArray(CompletableFuture[]::new);

            // Aguardar conclusão de todos os lotes
            CompletableFuture.allOf(futures).join();
            
            lastProcessingTime = Instant.now();
            healthy = true;
            lastError = null;
            
            logger.info("Processamento concluído com sucesso para {} lotes", lotes.size());
            
        } catch (Exception e) {
            healthy = false;
            lastError = e.getMessage();
            errosProcessamentoCounter.increment();
            logger.error("Erro no processamento de lotes", e);
            throw new ProcessamentoException("Falha no processamento de lotes", e);
        } finally {
            MDC.clear();
        }
    }

    private CompletableFuture<Void> processarLoteAsync(List<Conta> lote) {
        return CompletableFuture.runAsync(() -> {
            String loteId = UUID.randomUUID().toString();
            MDC.put("loteId", loteId);
            
            boolean acquired = false;
            try {
                // Tentar adquirir permit com timeout
                acquired = lotesSemaphore.tryAcquire(30, TimeUnit.SECONDS);
                if (!acquired) {
                    throw new ProcessamentoException("Timeout ao aguardar semáforo de lotes");
                }
                
                lotesSemaphoreUtilizacao.incrementAndGet();
                processarLote(lote);
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Thread interrompida durante processamento do lote {}", loteId, e);
                throw new ProcessamentoException("Processamento interrompido", e);
            } catch (Exception e) {
                errosProcessamentoCounter.increment();
                logger.error("Erro no processamento do lote {}", loteId, e);
                throw new ProcessamentoException("Falha no processamento do lote", e);
            } finally {
                if (acquired) {
                    lotesSemaphore.release();
                    lotesSemaphoreUtilizacao.decrementAndGet();
                }
                MDC.clear();
            }
        }, persistExecutor);
    }

    @Transactional
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void processarLote(List<Conta> lote) {
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            logger.debug("Processando lote com {} contas", lote.size());

            // Verificar circuit breaker se habilitado
            if (enableCircuitBreaker && !circuitBreakerService.isAvailable("database")) {
                throw new ProcessamentoException("Circuit breaker aberto para database");
            }

            // Persistência sequencial com circuit breaker
            executarComCircuitBreaker(() -> salvarInstituicoes(lote), "database");
            executarComCircuitBreaker(() -> salvarConsentimentos(lote), "database");
            executarComCircuitBreaker(() -> salvarContas(lote), "database");
            executarComCircuitBreaker(() -> salvarDetalhesContas(lote), "database");
            executarComCircuitBreaker(() -> salvarAssociacoes(lote), "database");

            // Envio assíncrono para Kafka
            enviarEventosKafkaAsync(lote);

            // Atualizar métricas
            lotesProcessadosCounter.increment();
            contasProcessadasCounter.increment(lote.size());
            
            logger.debug("Lote processado com sucesso: {} contas", lote.size());

        } catch (Exception e) {
            logger.error("Erro no processamento do lote", e);
            throw e;
        } finally {
            sample.stop(tempoProcessamentoLoteTimer);
        }
    }

    private void executarComCircuitBreaker(Runnable operacao, String serviceName) {
        if (enableCircuitBreaker) {
            circuitBreakerService.execute(operacao, serviceName);
        } else {
            operacao.run();
        }
    }

    private void enviarEventosKafkaAsync(List<Conta> lote) {
        lote.forEach(conta -> {
            CompletableFuture.runAsync(() -> {
                boolean acquired = false;
                try {
                    acquired = kafkaSemaphore.tryAcquire(10, TimeUnit.SECONDS);
                    if (!acquired) {
                        logger.warn("Timeout ao aguardar semáforo Kafka para conta {}", conta.getId());
                        return;
                    }
                    
                    kafkaSemaphoreUtilizacao.incrementAndGet();
                    enviarEventoKafka(conta);
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("Thread interrompida durante envio Kafka para conta {}", conta.getId(), e);
                } catch (Exception e) {
                    eventosKafkaFalhasCounter.increment();
                    logger.error("Erro no envio Kafka para conta {}", conta.getId(), e);
                } finally {
                    if (acquired) {
                        kafkaSemaphore.release();
                        kafkaSemaphoreUtilizacao.decrementAndGet();
                    }
                }
            }, kafkaExecutor);
        });
    }

    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 500, multiplier = 2))
    private void enviarEventoKafka(Conta conta) {
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            // Verificar circuit breaker para Kafka
            if (enableCircuitBreaker && !circuitBreakerService.isAvailable("kafka")) {
                throw new ProcessamentoException("Circuit breaker aberto para Kafka");
            }

            String eventoJson = conta.toJson();
            
            kafkaTemplate.send(kafkaTopicContasProcessadas, conta.getId().toString(), eventoJson)
                    .addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
                        @Override
                        public void onSuccess(SendResult<String, String> result) {
                            eventosKafkaEnviadosCounter.increment();
                            logger.debug("Evento Kafka enviado com sucesso para conta {}", conta.getId());
                        }

                        @Override
                        public void onFailure(Throwable ex) {
                            eventosKafkaFalhasCounter.increment();
                            logger.error("Falha no envio do evento Kafka para conta {}", conta.getId(), ex);
                        }
                    });

        } catch (Exception e) {
            eventosKafkaFalhasCounter.increment();
            logger.error("Erro no envio do evento Kafka para conta {}", conta.getId(), e);
            throw e;
        } finally {
            sample.stop(tempoEnvioKafkaTimer);
        }
    }

    // Métodos de persistência com logs estruturados
    private void salvarInstituicoes(List<Conta> lote) {
        logger.debug("Salvando instituições para {} contas", lote.size());
        // Implementação do batch update
        // jdbcTemplate.batchUpdate(sql, parameterSources);
    }

    private void salvarConsentimentos(List<Conta> lote) {
        logger.debug("Salvando consentimentos para {} contas", lote.size());
        // Implementação do batch update
    }

    private void salvarContas(List<Conta> lote) {
        logger.debug("Salvando contas: {}", lote.size());
        // Implementação do batch update
    }

    private void salvarDetalhesContas(List<Conta> lote) {
        logger.debug("Salvando detalhes das contas: {}", lote.size());
        // Implementação do batch update
    }

    private void salvarAssociacoes(List<Conta> lote) {
        logger.debug("Salvando associações para {} contas", lote.size());
        // Implementação do batch update
    }

    @PreDestroy
    public void shutdown() {
        logger.info("Iniciando shutdown graceful do ContaProcessor");
        
        try {
            // Parar de aceitar novos trabalhos
            persistExecutor.shutdown();
            kafkaExecutor.shutdown();
            
            // Aguardar conclusão dos trabalhos em andamento
            boolean persistTerminated = persistExecutor.awaitTermination(timeoutShutdownSegundos, TimeUnit.SECONDS);
            boolean kafkaTerminated = kafkaExecutor.awaitTermination(timeoutShutdownSegundos, TimeUnit.SECONDS);
            
            if (!persistTerminated) {
                logger.warn("Executor de persistência não terminou no tempo esperado, forçando shutdown");
                persistExecutor.shutdownNow();
            }
            
            if (!kafkaTerminated) {
                logger.warn("Executor de Kafka não terminou no tempo esperado, forçando shutdown");
                kafkaExecutor.shutdownNow();
            }
            
            logger.info("Shutdown do ContaProcessor concluído");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Shutdown interrompido", e);
            persistExecutor.shutdownNow();
            kafkaExecutor.shutdownNow();
        }
    }

    @Override
    public Health health() {
        Health.Builder builder = healthy ? Health.up() : Health.down();
        
        builder.withDetail("lastProcessingTime", lastProcessingTime)
               .withDetail("lotesSemaphoreAvailable", lotesSemaphore.availablePermits())
               .withDetail("kafkaSemaphoreAvailable", kafkaSemaphore.availablePermits())
               .withDetail("persistExecutorTerminated", persistExecutor.isTerminated())
               .withDetail("kafkaExecutorTerminated", kafkaExecutor.isTerminated());
        
        if (lastError != null) {
            builder.withDetail("lastError", lastError);
        }
        
        return builder.build();
    }

    // Getters para testes
    public int getLoteSize() { return loteSize; }
    public int getMaxLotesSimultaneos() { return maxLotesSimultaneos; }
    public int getMaxKafkaSimultaneos() { return maxKafkaSimultaneos; }
}

