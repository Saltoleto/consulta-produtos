import java.util.ArrayList;
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

        // Divide em lotes de 100
        List<List<Conta>> lotes = particionar(contas, 100);

        List<CompletableFuture<Void>> futures = lotes.stream()
            .map(lote ->
                // 1. salvar o batch
                CompletableFuture.runAsync(() -> salvarBatch(lote), executor)
                    // 2. após salvar, enviar eventos Kafka
                    .thenRunAsync(() -> enviarKafkaBatch(lote), executor)
            )
            .toList();

        // Espera todos os lotes terminarem
        futures.forEach(CompletableFuture::join);
    }

    private void salvarBatch(List<Conta> lote) {
        System.out.println("Salvando batch de " + lote.size() + " contas");
        contaRepository.saveAll(lote); // salva em lote
    }

    private void enviarKafkaBatch(List<Conta> lote) {
        lote.forEach(conta -> {
            System.out.println("Enviando evento da conta " + conta.getId() + " para o Kafka");
            kafkaProducer.send(conta);
        });
    }

    // Helper para particionar a lista em sublistas de 100
    private List<List<Conta>> particionar(List<Conta> contas, int tamanhoLote) {
        List<List<Conta>> lotes = new ArrayList<>();
        for (int i = 0; i < contas.size(); i += tamanhoLote) {
            lotes.add(contas.subList(i, Math.min(i + tamanhoLote, contas.size())));
        }
        return lotes;
    }
}
