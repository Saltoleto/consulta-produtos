import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public class ContaProcessor {

    private final ContaRepository contaRepository;
    private final KafkaProducer kafkaProducer;

    public ContaProcessor(ContaRepository contaRepository, KafkaProducer kafkaProducer) {
        this.contaRepository = contaRepository;
        this.kafkaProducer = kafkaProducer;
    }

    public void processarContas(List<Conta> contas) {
        var executor = Executors.newVirtualThreadPerTaskExecutor();

        List<CompletableFuture<Void>> futures = contas.stream()
            .map(conta -> 
                // 1. salvar no banco
                CompletableFuture.runAsync(() -> salvarConta(conta), executor)
                    // 2. após salvar, enviar para Kafka
                    .thenRunAsync(() -> enviarKafka(conta), executor)
            )
            .toList();

        // espera todas as contas terminarem
        futures.forEach(CompletableFuture::join);
    }

    private void salvarConta(Conta conta) {
        System.out.println("Salvando conta " + conta.getId());
        contaRepository.save(conta);
    }

    private void enviarKafka(Conta conta) {
        System.out.println("Enviando evento da conta " + conta.getId() + " para o Kafka");
        kafkaProducer.send(conta);
    }
}
