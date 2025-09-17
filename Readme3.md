import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.listener.acknowledgement.Acknowledgement;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class ContaProcessor {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;

    // Virtual threads para persistência e Kafka
    private final ExecutorService persistExecutor = Executors.newVirtualThreadPerTaskExecutor();
    private final ExecutorService kafkaExecutor = Executors.newVirtualThreadPerTaskExecutor();

    public ContaProcessor(NamedParameterJdbcTemplate jdbcTemplate,
                          KafkaTemplate<String, String> kafkaTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    // Listener manual com ack
    @SqsListener("fila-contas")
    public void processarMensagem(List<Conta> contas, Acknowledgement ack) {
        try {
            // Divide em lotes de 100 contas
            List<List<Conta>> lotes = ListUtils.partition(contas, 100);

            // Submete cada lote em paralelo
            List<CompletableFuture<Void>> futures = lotes.stream()
                .map(lote -> CompletableFuture.runAsync(() -> processarLote(lote), persistExecutor))
                .collect(Collectors.toList());

            // Espera todos os lotes finalizarem
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            ack.acknowledge(); // ✅ só confirma após persistência + Kafka de todos os lotes
        } catch (Exception e) {
            throw new RuntimeException("Erro no processamento da mensagem SQS", e);
        }
    }

    @Transactional
    public void processarLote(List<Conta> lote) {
        salvarInstituicoes(lote);
        salvarConsentimentos(lote);
        salvarContas(lote);
        salvarDetalhesContas(lote);
        salvarAssociacoes(lote);

        // Kafka assíncrono, mas aguardamos todos antes de encerrar o lote
        List<CompletableFuture<Void>> futures = lote.stream()
            .map(conta -> CompletableFuture.runAsync(() -> 
                kafkaTemplate.send("contas.processadas", conta.getId().toString(), conta.toJson()).join(),
                kafkaExecutor))
            .collect(Collectors.toList());

        // Espera todos os envios Kafka do lote
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private void salvarInstituicoes(List<Conta> lote) { /* batchUpdate */ }
    private void salvarConsentimentos(List<Conta> lote) { /* batchUpdate */ }
    private void salvarContas(List<Conta> lote) { /* batchUpdate */ }
    private void salvarDetalhesContas(List<Conta> lote) { /* batchUpdate */ }
    private void salvarAssociacoes(List<Conta> lote) { /* batchUpdate */ }
}
