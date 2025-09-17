Show, então precisamos atender **dois cenários**:

1. **JdbcTemplate/BatchUpdate** → você escolhe manualmente se vai para o `reader` ou `writer` antes de cada operação.
2. **JPA/Hibernate** → você deixa automático, Spring decide baseado no tipo de query (`SELECT` → reader, `INSERT/UPDATE/DELETE` → writer).

Vou te mostrar a estrutura completa para os **dois juntos**.

---

## `application.yml`

```yaml
spring:
  datasource:
    writer:
      url: jdbc:mysql://writer-endpoint-aurora.cluster-xxxxxx.us-east-1.rds.amazonaws.com:3306/minha_base
      username: meu_usuario
      password: minha_senha
      driver-class-name: com.mysql.cj.jdbc.Driver
      hikari:
        maximum-pool-size: 50
        minimum-idle: 10
        idle-timeout: 30000
        max-lifetime: 600000
        connection-timeout: 30000

    reader:
      url: jdbc:mysql://reader-endpoint-aurora.cluster-ro-xxxxxx.us-east-1.rds.amazonaws.com:3306/minha_base
      username: meu_usuario
      password: minha_senha
      driver-class-name: com.mysql.cj.jdbc.Driver
      hikari:
        maximum-pool-size: 50
        minimum-idle: 10
        idle-timeout: 30000
        max-lifetime: 600000
        connection-timeout: 30000

  jpa:
    hibernate:
      ddl-auto: none
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

---

## Classe de roteamento

```java
public class ReplicationRoutingDataSource extends AbstractRoutingDataSource {

    private static final ThreadLocal<DatabaseType> CONTEXT = new ThreadLocal<>();

    public static void setDataSource(DatabaseType type) {
        CONTEXT.set(type);
    }

    public static void clear() {
        CONTEXT.remove();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return CONTEXT.get() == null ? DatabaseType.WRITER : CONTEXT.get();
    }

    public enum DatabaseType {
        WRITER, READER
    }
}
```

---

## Configuração

```java
@Configuration
public class DataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.writer")
    public DataSource writerDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.reader")
    public DataSource readerDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public DataSource routingDataSource(
            @Qualifier("writerDataSource") DataSource writer,
            @Qualifier("readerDataSource") DataSource reader) {

        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(ReplicationRoutingDataSource.DatabaseType.WRITER, writer);
        targetDataSources.put(ReplicationRoutingDataSource.DatabaseType.READER, reader);

        ReplicationRoutingDataSource routingDataSource = new ReplicationRoutingDataSource();
        routingDataSource.setDefaultTargetDataSource(writer);
        routingDataSource.setTargetDataSources(targetDataSources);
        return routingDataSource;
    }

    // Usado pelo Hibernate/JPA
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("routingDataSource") DataSource routingDataSource) {
        return builder
                .dataSource(routingDataSource)
                .packages("com.seuprojeto.modelo")
                .build();
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    // Usado pelo JdbcTemplate
    @Bean
    public NamedParameterJdbcTemplate jdbcTemplate(@Qualifier("routingDataSource") DataSource routingDataSource) {
        return new NamedParameterJdbcTemplate(routingDataSource);
    }
}
```

---

## Uso no **JdbcTemplate**

```java
// SELECT → vai automaticamente para o Reader
ReplicationRoutingDataSource.setDataSource(ReplicationRoutingDataSource.DatabaseType.READER);
List<Conta> contas = jdbcTemplate.query("SELECT * FROM contas", rowMapper);
ReplicationRoutingDataSource.clear();

// INSERT → vai automaticamente para o Writer
ReplicationRoutingDataSource.setDataSource(ReplicationRoutingDataSource.DatabaseType.WRITER);
jdbcTemplate.update("INSERT INTO contas ...", params);
ReplicationRoutingDataSource.clear();
```

---

## Uso no **JPA/Hibernate**

Com o `EntityManager` ou `Spring Data JPA`:

```java
@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {
    List<Conta> findByInstituicao(String instituicao);
}
```

* Quando você chama `findBy...` → Hibernate gera `SELECT` → vai para o **reader**.
* Quando você chama `save` ou `delete` → Hibernate gera `INSERT/UPDATE/DELETE` → vai para o **writer**.

Isso acontece porque o Hibernate consegue diferenciar automaticamente a operação SQL.

---

✅ Resumindo:

* **JdbcTemplate** → você indica manualmente se é leitura/escrita antes de cada operação.
* **Hibernate/JPA** → Spring decide automaticamente.

---

Quer que eu prepare uma versão do **AOP (aspect)** para que até mesmo no **JdbcTemplate** não precise chamar `ReplicationRoutingDataSource.setDataSource(...)` manualmente (ou seja, o sistema inspeciona a query e escolhe reader/writer sozinho)?
