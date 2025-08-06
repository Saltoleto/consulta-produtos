# Sistema de Consulta de Produtos e Operadores

## Visão Geral

Este projeto implementa um sistema de consulta de produtos associados a operadores, desenvolvido com Java 21 e Spring Boot. O sistema permite gerenciar relacionamentos many-to-many entre duas entidades principais: Operador e Produto, utilizando Records do Java para uma abordagem mais moderna e concisa.

## Funcionalidades Principais

### 🎯 Consulta Principal
- **Produtos associados a um operador**: Busca todos os produtos que um operador específico possui

### 📋 Funcionalidades Adicionais
- CRUD completo para Operadores e Produtos
- Gerenciamento de associações entre Operador e Produto
- Consultas com filtros (nome, categoria, faixa de preço)
- Paginação em todas as listagens
- Soft delete para manter integridade referencial
- Auditoria automática (data de criação e atualização)
- **Records do Java**: DTOs modernos e imutáveis

## Tecnologias Utilizadas

- **Java 21**: Linguagem de programação com Records
- **Spring Boot 3.x**: Framework principal
- **Spring Data JPA**: Persistência de dados
- **Spring Web**: APIs REST
- **H2 Database**: Banco de dados em memória (desenvolvimento)
- **JUnit 5**: Testes unitários
- **Mockito**: Mocks para testes
- **Maven**: Gerenciamento de dependências

## Arquitetura

O projeto segue uma arquitetura em camadas bem definida:

```
├── controller/     # Controladores REST
├── service/        # Lógica de negócio
├── repository/     # Acesso a dados
├── entity/         # Entidades JPA
├── dto/            # Records (Request/Response)
├── mapper/         # Conversores entre entidades e Records
└── config/         # Configurações
```

## Modelo de Dados

### Entidades Principais

1. **Operador**: Representa operadores do sistema
2. **Produto**: Representa produtos que podem ser associados

### Relacionamentos

- **Operador ↔ Produto**: Many-to-Many (via OperadorProduto)

## Estrutura do Projeto

```
src/
├── main/java/com/exemplo/consultaprodutos/
│   ├── entity/
│   │   ├── BaseEntity.java
│   │   ├── Operador.java
│   │   ├── Produto.java
│   │   └── OperadorProduto.java
│   ├── repository/
│   │   ├── OperadorRepository.java
│   │   ├── ProdutoRepository.java
│   │   └── OperadorProdutoRepository.java
│   ├── service/
│   │   ├── OperadorService.java
│   │   ├── ProdutoService.java
│   │   └── AssociacaoService.java
│   ├── controller/
│   │   ├── OperadorController.java
│   │   ├── ProdutoController.java
│   │   ├── AssociacaoController.java
│   │   └── GlobalExceptionHandler.java
│   ├── dto/
│   │   ├── request/     # Records para entrada
│   │   └── response/    # Records para saída
│   ├── mapper/
│   └── config/
└── test/
    └── java/com/exemplo/consultaprodutos/
        ├── service/
        └── repository/
```

## Instalação e Execução

### Pré-requisitos

- Java 21 ou superior
- Maven 3.6 ou superior

### Passos para Execução

1. **Clone o repositório**
```bash
git clone <url-do-repositorio>
cd projeto-consulta-produtos
```

2. **Compile o projeto**
```bash
mvn clean compile
```

3. **Execute os testes**
```bash
mvn test
```

4. **Execute a aplicação**
```bash
mvn spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080`

## Documentação da API

### Endpoints Principais

#### 🔍 Consulta Principal

**Buscar produtos associados a um operador**
```http
GET /api/operadores/{id}/produtos
```

#### 👥 Operadores

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/operadores` | Lista todos os operadores |
| GET | `/api/operadores/{id}` | Busca operador por ID |
| POST | `/api/operadores` | Cria novo operador |
| PUT | `/api/operadores/{id}` | Atualiza operador |
| DELETE | `/api/operadores/{id}` | Remove operador (soft delete) |
| GET | `/api/operadores/{id}/produtos` | **Produtos do operador** |

#### 📦 Produtos

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/produtos` | Lista todos os produtos |
| GET | `/api/produtos/{id}` | Busca produto por ID |
| POST | `/api/produtos` | Cria novo produto |
| PUT | `/api/produtos/{id}` | Atualiza produto |
| DELETE | `/api/produtos/{id}` | Remove produto (soft delete) |
| GET | `/api/produtos/categorias` | Lista categorias |

#### 🔗 Associações

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/associacoes/operador-produto` | Associa produto a operador |
| DELETE | `/api/associacoes/operador-produto/{opId}/{prodId}` | Remove associação |

## Exemplos de Uso com Records

### 1. Criar um Operador

```http
POST /api/operadores
Content-Type: application/json

{
  "nome": "João Silva",
  "email": "joao@empresa.com",
  "telefone": "11999999999"
}
```

### 2. Criar um Produto

```http
POST /api/produtos
Content-Type: application/json

{
  "nome": "Smartphone Galaxy",
  "descricao": "Smartphone Android com 128GB",
  "codigo": "SMART001",
  "preco": 899.99,
  "categoria": "Eletrônicos"
}
```

### 3. Associar Produto a Operador

```http
POST /api/associacoes/operador-produto
Content-Type: application/json

{
  "primeiraEntidadeId": 1,
  "segundaEntidadeId": 1
}
```

### 4. Buscar Produtos de um Operador

```http
GET /api/operadores/1/produtos
```

**Resposta:**
```json
[
  {
    "id": 1,
    "nome": "Smartphone Galaxy",
    "codigo": "SMART001",
    "preco": 899.99,
    "categoria": "Eletrônicos",
    "totalOperadores": 1
  }
]
```

## Vantagens dos Records

### 🚀 Benefícios Implementados

- **Imutabilidade**: Records são imutáveis por padrão
- **Menos código**: Eliminação de getters, setters, equals, hashCode
- **Validação integrada**: Bean Validation funciona perfeitamente
- **Serialização automática**: JSON serialization/deserialization
- **Construtores compactos**: Validações e normalizações no construtor

### 📝 Exemplo de Record

```java
public record OperadorRequestDTO(
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100)
    String nome,
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter formato válido")
    String email,
    
    @Pattern(regexp = "\\d{10,11}")
    String telefone
) {
    // Construtor compacto para normalizações
    public OperadorRequestDTO {
        if (telefone != null) {
            telefone = telefone.replaceAll("[^\\d]", "");
        }
        if (email != null) {
            email = email.toLowerCase().trim();
        }
    }
}
```

## Filtros e Paginação

### Filtros Disponíveis

**Operadores:**
- `?nome=João` - Busca por nome (parcial)

**Produtos:**
- `?nome=Smartphone` - Busca por nome (parcial)
- `?categoria=Eletrônicos` - Busca por categoria
- `?precoMin=100&precoMax=1000` - Busca por faixa de preço

### Paginação

Adicione `/paginado` ao endpoint e use os parâmetros:
- `?page=0` - Número da página (inicia em 0)
- `?size=20` - Tamanho da página
- `?sort=nome,asc` - Ordenação

**Exemplo:**
```http
GET /api/produtos/paginado?page=0&size=10&sort=nome,asc
```

## Tratamento de Erros

A API retorna erros padronizados:

```json
{
  "titulo": "Erro de validação",
  "mensagem": "Os dados fornecidos são inválidos",
  "status": 400,
  "timestamp": "2024-01-15T10:30:00",
  "detalhes": {
    "email": "Email deve ter formato válido",
    "nome": "Nome é obrigatório"
  }
}
```

## Testes

O projeto inclui testes unitários e de integração:

### Executar Testes

```bash
# Todos os testes
mvn test

# Com relatório de cobertura
mvn test jacoco:report
```

### Estrutura de Testes

- **Testes de Service**: Testam a lógica de negócio com mocks
- **Testes de Repository**: Testam consultas JPA com banco em memória

## Boas Práticas Implementadas

### 🏗️ Arquitetura

- **Separação de responsabilidades**: Cada camada tem sua responsabilidade específica
- **Records**: Uso de Records para DTOs imutáveis e concisos
- **Injeção de dependência**: Uso do Spring para gerenciar dependências
- **Mappers**: Conversão limpa entre entidades e Records

### 💾 Persistência

- **Soft Delete**: Registros são marcados como inativos ao invés de removidos
- **Auditoria**: Campos automáticos de data de criação e atualização
- **Índices**: Índices otimizados para consultas frequentes
- **Relacionamentos explícitos**: Tabela de junção com campos adicionais

### 🔒 Validação

- **Bean Validation**: Validações declarativas nos Records
- **Construtores compactos**: Normalizações automáticas
- **Validações de negócio**: Regras específicas nos services
- **Tratamento de exceções**: Handler global para padronizar erros

## Considerações Técnicas

### Banco de Dados

O projeto está configurado para usar H2 em memória para desenvolvimento. Para produção, configure um banco relacional:

**application-prod.properties:**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/consultaprodutos
spring.datasource.username=usuario
spring.datasource.password=senha
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

### Melhorias Futuras

1. **Segurança**: Implementar Spring Security com JWT
2. **Cache**: Adicionar Redis para cache de consultas frequentes
3. **Documentação**: Integrar Swagger/OpenAPI
4. **Monitoramento**: Adicionar Actuator e métricas
5. **Containerização**: Dockerfile e docker-compose

## Estrutura de Dados

### Diagrama ER

```
┌─────────────┐    ┌──────────────────┐    ┌─────────────┐
│   Operador  │────│ OperadorProduto  │────│   Produto   │
└─────────────┘    └──────────────────┘    └─────────────┘
```

### Campos Principais

**BaseEntity (herdada por todas):**
- `id`: Identificador único
- `dataCriacao`: Data de criação automática
- `dataAtualizacao`: Data de atualização automática
- `ativo`: Flag para soft delete

**Operador:**
- `nome`: Nome do operador
- `email`: Email único
- `telefone`: Telefone de contato

**Produto:**
- `nome`: Nome do produto
- `descricao`: Descrição detalhada
- `codigo`: Código único do produto
- `preco`: Preço (BigDecimal)
- `categoria`: Categoria do produto

## Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -am 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

## Licença

Este projeto está sob a licença MIT. Veja o arquivo LICENSE para mais detalhes.

