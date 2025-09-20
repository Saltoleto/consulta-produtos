Perfeito! Aqui está um **README.md completo** para o seu projeto, pronto para GitHub:

---

# Conta Persistência Benchmark

## 🚀 Descrição

Este projeto demonstra uma **aplicação de alta performance em Java 21** para persistência massiva de registros de contas correntes em um banco MySQL Aurora, utilizando:

* **Threads Virtuais** (Virtual Threads)
* **Batch Inserts** com JDBC
* **INSERT IGNORE** para evitar duplicidades
* **Spring Boot 3.2**

O objetivo do projeto é atingir alta taxa de inserção, **benchmark de 30.000 registros por segundo**.

O projeto inclui também uma configuração **local com Docker Compose** para MySQL Writer + Reader, simulando um ambiente de replicação.

---

## 🧰 Tecnologias utilizadas

* **Java 21**
* **Spring Boot 3.2**
* **Spring JDBC (JdbcTemplate e NamedParameterJdbcTemplate)**
* **MySQL 8 / Aurora MySQL**
* **Docker Compose**
* **Threads Virtuais** (Java 21)
* **Batch Inserts** para alta performance

---

## 📂 Estrutura do projeto

```
conta-persistencia/
├── docker-compose.yml
├── pom.xml
└── src
    ├── main
    │   ├── java
    │   │   └── com/exemplo/conta
    │   │       ├── ContaPersistenciaApplication.java
    │   │       ├── controller/BenchmarkController.java
    │   │       ├── model/ContaCorrente.java
    │   │       ├── repository/ContaRepository.java
    │   │       └── service/ContaService.java
    │   └── resources
    │       ├── application.yml
    │       └── schema.sql
    └── test/java
```

---

## ⚡ Funcionalidades

1. **Persistência massiva de contas correntes**
2. **Configuração para Writer + Reader MySQL**
3. **Benchmark de inserção** via endpoint REST
4. **Controle de duplicidade** via `INSERT IGNORE`

---

## 🛠️ Configuração local

1. **Clone o projeto:**

```bash
git clone https://github.com/Saltoleto/consulta-produtos.git
cd consulta-produtos
```

2. **Suba os containers do MySQL localmente:**

```bash
docker-compose up -d
```

Isso irá criar dois bancos:

* `mysql-writer` → porta 3307
* `mysql-reader` → porta 3308

3. **Rodar a aplicação Spring Boot:**

```bash
mvn spring-boot:run
```

---

## 🚀 Testando o benchmark

A aplicação expõe um endpoint REST para testes de inserção:

```bash
# Inserir 30k registros sem INSERT IGNORE
curl -X POST "http://localhost:8080/benchmark?quantidade=30000&usarIgnore=false"

# Inserir 30k registros usando INSERT IGNORE
curl -X POST "http://localhost:8080/benchmark?quantidade=30000&usarIgnore=true"
```

O retorno inclui:

* Quantidade de registros persistidos
* Tempo total de inserção
* Taxa de registros por segundo

---

## 📈 Resultados

* **Throughput atingido:** 30.000 registros por segundo
* **Java 21 + Threads Virtuais** → paralelismo massivo
* **Batch Inserts** → reduz overhead de rede
* **INSERT IGNORE** → evita duplicidade sem checagem prévia

---

## 🔗 Link do projeto

[GitHub - Conta Persistência Benchmark](https://github.com/Saltoleto/consulta-produtos/tree/main)

---

## 💡 Observações

* Para testes de performance mais realistas, ajuste os **tamanhos de batch** e o número de threads virtuais.
* Monitore **CPU, memória e I/O do banco** para evitar gargalos.
* Ideal para sistemas **financeiros ou de grande volume de dados**.

---

Se quiser, posso gerar também uma **versão resumida e mais “bonita” para GitHub**, com badges, seções de benchmark e setup rápido, que fica visualmente mais atrativa.

Quer que eu faça essa versão “GitHub-friendly”?
