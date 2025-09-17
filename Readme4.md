Boa! 👌 Como você está usando **Aurora MySQL** com uma instância de **escrita** (writer) e uma ou mais de **leitura** (read replicas), a configuração no `application.yml` pode ser feita de algumas formas.

No Spring Boot, se você quiser separar **datasources de leitura e escrita**, o mais comum é:

1. Configurar dois `DataSource` distintos (um para leitura e outro para escrita).
2. Criar um `RoutingDataSource` (se precisar que a escolha seja automática).
3. Ou simplesmente injetar manualmente cada datasource e decidir no código qual usar.

Aqui vai um exemplo **simples** de configuração no `application.yml` para leitura e escrita:

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
```

---

### Como usar no código

* Para acessar o banco de **escrita** (`writer`):
  Use o `JdbcTemplate` ou `NamedParameterJdbcTemplate` configurado com o datasource de escrita.
* Para acessar o banco de **leitura** (`reader`):
  Configure outro `JdbcTemplate` para consultas.

Exemplo de configuração em Java:

```java
@Configuration
public class DataSourceConfig {

    @Bean(name = "writerDataSource")
    @ConfigurationProperties("spring.datasource.writer")
    public DataSource writerDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "readerDataSource")
    @ConfigurationProperties("spring.datasource.reader")
    public DataSource readerDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public NamedParameterJdbcTemplate writerJdbcTemplate(@Qualifier("writerDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean
    public NamedParameterJdbcTemplate readerJdbcTemplate(@Qualifier("readerDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
```

Assim você terá dois `JdbcTemplate`:

* `writerJdbcTemplate` → para `INSERT`, `UPDATE`, `DELETE`.
* `readerJdbcTemplate` → para `SELECT`.

---

👉 Pergunta: você quer que o **roteamento entre leitura e escrita seja automático** (Spring escolhendo se vai para réplica ou writer dependendo do tipo de query), ou prefere controlar **manualmente** no código qual datasource usar?
