package com.example.batchimport;

import org.mapstruct.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class ImportContasBatchService {

    private static final Logger log = LoggerFactory.getLogger(ImportContasBatchService.class);

    private final NamedParameterJdbcTemplate jdbc;
    private final KafkaTemplate<String, Object> kafka;
    private final ContaMapper contaMapper;
    private ThreadPoolTaskExecutor executor;

    private static final int LOTE_SIZE = 500;

    public ImportContasBatchService(NamedParameterJdbcTemplate jdbc,
                                    KafkaTemplate<String, Object> kafka,
                                    ContaMapper contaMapper) {
        this.jdbc = jdbc;
        this.kafka = kafka;
        this.contaMapper = contaMapper;
    }

    // Configuração do executor interno
    @PostConstruct
    private void initExecutor() {
        executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("BatchKafka-");
        executor.initialize();
    }

    public void importarContas(List<AccountDTO> itauAccounts,
                               List<AccountDTO> opfAccounts,
                               List<String> produtosRevogados) {

        Instant start = Instant.now();
        log.info("Iniciando import: itau={} opf={} revogadas={}",
                itauAccounts.size(), opfAccounts.size(), produtosRevogados.size());

        processaEmLotes(itauAccounts, "ITAU");
        processaEmLotes(opfAccounts, "OPF");
        processaRevogadas(produtosRevogados);

        log.info("Import finalizado em {} ms", Duration.between(start, Instant.now()).toMillis());
    }

    private void processaEmLotes(List<AccountDTO> accounts, String tipo) {
        if (accounts == null || accounts.isEmpty()) return;

        int total = accounts.size();
        for (int i = 0; i < total; i += LOTE_SIZE) {
            int toIndex = Math.min(i + LOTE_SIZE, total);
            List<AccountDTO> lote = accounts.subList(i, toIndex);
            Instant s = Instant.now();
            try {
                processaLoteTransactional(lote, tipo);
                log.info("Lote processado tipo={} tamanho={} tempoMs={}", tipo, lote.size(),
                        Duration.between(s, Instant.now()).toMillis());
            } catch (Exception e) {
                log.error("Erro processando lote tipo={} tamanho={}", tipo, lote.size(), e);
                throw new RuntimeException(e);
            }
        }
    }

    @Transactional
    public void processaLoteTransactional(List<AccountDTO> lote, String tipo) {
        Set<String> contasExistentes = buscaContasExistentes(lote);

        upsertContasBatch(lote, tipo, contasExistentes);
        upsertDetalhesBatch(lote);
        upsertUsuarioContaBatch(lote);

        if ("OPF".equalsIgnoreCase(tipo)) {
            upsertConsentimentosBatch(lote);
        }

        enviarEventosAsync(lote);
    }

    private Set<String> buscaContasExistentes(List<AccountDTO> lote) {
        String sql = "SELECT cod_idt_conta FROM contas WHERE cod_idt_conta IN (:ids)";
        List<String> ids = lote.stream().map(a -> a.codIdtConta).toList();
        List<String> existentes = jdbc.queryForList(sql, new MapSqlParameterSource("ids", ids), String.class);
        return new HashSet<>(existentes);
    }

    private void upsertContasBatch(List<AccountDTO> lote, String tipo, Set<String> existentes) {
        if (lote.isEmpty()) return;

        String sqlInsert = "INSERT INTO contas (cod_idt_conta, tipo, datahora_criacao, datahora_alteracao) " +
                "VALUES (:codIdtConta, :tipo, NOW(), NOW()) " +
                "ON DUPLICATE KEY UPDATE datahora_alteracao = NOW()";

        SqlParameterSource[] batch = lote.stream()
                .map(a -> new MapSqlParameterSource()
                        .addValue("codIdtConta", a.codIdtConta)
                        .addValue("tipo", tipo))
                .toArray(SqlParameterSource[]::new);

        jdbc.batchUpdate(sqlInsert, batch);
    }

    private void upsertDetalhesBatch(List<AccountDTO> lote) {
        if (lote.isEmpty()) return;

        String sql = "INSERT INTO conta_detalhes (conta_id, campo1, campo2) " +
                "VALUES (:contaId, :campo1, :campo2) " +
                "ON DUPLICATE KEY UPDATE campo1 = VALUES(campo1), campo2 = VALUES(campo2)";

        List<SqlParameterSource> batchList = lote.stream().map(a ->
                new MapSqlParameterSource()
                        .addValue("contaId", a.codIdtConta)
                        .addValue("campo1", a.detalhe != null ? a.detalhe.campo1 : null)
                        .addValue("campo2", a.detalhe != null ? a.detalhe.campo2 : null)
        ).toList();

        jdbc.batchUpdate(sql, batchList.toArray(SqlParameterSource[]::new));
    }

    private void upsertConsentimentosBatch(List<AccountDTO> lote) {
        if (lote.isEmpty()) return;

        String sql = "INSERT INTO consentimentos (conta_id, consent, datahora_criacao) " +
                "VALUES (:contaId, :consent, NOW()) " +
                "ON DUPLICATE KEY UPDATE consent = VALUES(consent), datahora_criacao = NOW()";

        List<SqlParameterSource> batchList = lote.stream()
                .filter(a -> a.consent != null)
                .map(a -> new MapSqlParameterSource()
                        .addValue("contaId", a.codIdtConta)
                        .addValue("consent", a.consent.payload)
                ).toList();

        jdbc.batchUpdate(sql, batchList.toArray(SqlParameterSource[]::new));
    }

    private void upsertUsuarioContaBatch(List<AccountDTO> lote) {
        if (lote.isEmpty()) return;

        String sql = "INSERT IGNORE INTO usuario_conta (usuario_id, conta_id) VALUES (:usuarioId, :contaId)";

        List<SqlParameterSource> batchList = lote.stream()
                .map(a -> new MapSqlParameterSource()
                        .addValue("usuarioId", a.usuarioId)
                        .addValue("contaId", a.codIdtConta)
                ).toList();

        jdbc.batchUpdate(sql, batchList.toArray(SqlParameterSource[]::new));
    }

    private void enviarEventosAsync(List<AccountDTO> lote) {
        for (AccountDTO a : lote) {
            CompletableFuture.runAsync(() -> {
                Optional<ContaUsuarioView> viewOpt = carregarContaUsuario(a.codIdtConta);
                viewOpt.ifPresent(v -> {
                    ContaEvento contaEvento = contaMapper.toContaEvento(v);
                    UsuarioEvento usuarioEvento = contaMapper.toUsuarioEvento(v);
                    kafka.send("conta-criada", contaEvento.id(),
                            Map.of("usuario", usuarioEvento, "conta", contaEvento));
                    log.debug("Evento conta-criada enviado para conta {} usuario {}", v.codIdtConta(), v.usuarioId());
                });
            }, executor);
        }
    }

    private Optional<ContaUsuarioView> carregarContaUsuario(String codIdt) {
        String sql = "SELECT c.cod_idt_conta, c.tipo, u.usuario_id " +
                "FROM contas c " +
                "JOIN usuario_conta u ON u.conta_id = c.cod_idt_conta " +
                "WHERE c.cod_idt_conta = :codIdt";

        MapSqlParameterSource params = new MapSqlParameterSource("codIdt", codIdt);
        List<ContaUsuarioView> list = jdbc.query(sql, params, (rs, i) ->
                new ContaUsuarioView(rs.getString("cod_idt_conta"), rs.getString("tipo"), rs.getLong("usuario_id"))
        );
        return list.stream().findFirst();
    }

    public void processaRevogadas(List<String> produtosRevogados) {
        if (produtosRevogados == null || produtosRevogados.isEmpty()) return;

        String sqlDelete = "DELETE FROM usuario_conta WHERE conta_id IN (:ids)";
        jdbc.update(sqlDelete, new MapSqlParameterSource("ids", produtosRevogados));

        for (String codIdt : produtosRevogados) {
            CompletableFuture.runAsync(() -> {
                Optional<ContaUsuarioView> view = carregarContaUsuario(codIdt);
                view.ifPresent(v -> {
                    ContaEvento contaEvento = contaMapper.toContaEvento(v);
                    UsuarioEvento usuarioEvento = contaMapper.toUsuarioEvento(v);
                    kafka.send("conta-revogada", contaEvento.id(),
                            Map.of("usuario", usuarioEvento, "conta", contaEvento));
                    log.debug("Evento conta-revogada enviado para conta {} usuario {}", v.codIdtConta(), v.usuarioId());
                });
            }, executor);
        }
    }

    // DTOs e Records
    public static record AccountDTO(String codIdtConta, Long usuarioId, DetalheDTO detalhe, ConsentDTO consent) {}
    public static record DetalheDTO(String campo1, String campo2) {}
    public static record ConsentDTO(String payload) {}
    public static record ContaUsuarioView(String codIdtConta, String tipo, Long usuarioId) {}

    public static record ContaEvento(String id, String codIdtConta, String tipo) {}
    public static record UsuarioEvento(Long id) {}
}

// Mapper MapStruct
@Mapper(componentModel = "spring")
interface ContaMapper {
    ImportContasBatchService.ContaEvento toContaEvento(ImportContasBatchService.ContaUsuarioView view);
    ImportContasBatchService.UsuarioEvento toUsuarioEvento(ImportContasBatchService.ContaUsuarioView view);
}
