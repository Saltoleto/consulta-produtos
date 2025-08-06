# Sistema de Consulta de Produtos e Operadores

## Visão Geral

Este projeto implementa um sistema de consulta de produtos associados a operadores e usuários associados a produtos, desenvolvido com Java 21 e Spring Boot. O sistema permite gerenciar relacionamentos many-to-many entre três entidades principais: Operador, Produto e Usuário.

## Funcionalidades Principais

### 🎯 Consultas Principais
- **Produtos associados a um operador**: Busca todos os produtos que um operador específico possui
- **Usuários associados a um produto**: Busca todos os usuários que possuem um produto específico

### 📋 Funcionalidades Adicionais
- CRUD completo para Operadores, Produtos e Usuários
- Gerenciamento de associações entre entidades
- Consultas com filtros (nome, categoria, faixa de preço)
- Paginação em todas as listagens
- Soft delete para manter integridade referencial
- Auditoria automática (data de criação e atualização)

## Tecnologias Utilizadas

- **Java 21**: Linguagem de programação
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
├── dto/            # Data Transfer Objects
├── mapper/         # Conversores entre entidades e DTOs
└── config/         # Configurações
```

## Modelo de Dados

### Entidades Principais

1. **Operador**: Representa operadores do sistema
2. **Produto**: Representa produtos que podem ser associados
3. **Usuário**: Representa usuários que podem ter produtos

### Relacionamentos

- **Operador ↔ Produto**: Many-to-Many (via OperadorProduto)
- **Usuário ↔ Produto**: Many-to-Many (via UsuarioProduto)

## Estrutura do Projeto

```
src/
├── main/java/com/exemplo/consultaprodutos/
│   ├── entity/
│   │   ├── BaseEntity.java
│   │   ├── Operador.java
│   │   ├── Produto.java
│   │   ├── Usuario.java
│   │   ├── OperadorProduto.java
│   │   └── UsuarioProduto.java
│   ├── repository/
│   │   ├── OperadorRepository.java
│   │   ├── ProdutoRepository.java
│   │   ├── UsuarioRepository.java
│   │   ├── OperadorProdutoRepository.java
│   │   └── UsuarioProdutoRepository.java
│   ├── service/
│   │   ├── OperadorService.java
│   │   ├── ProdutoService.java
│   │   ├── UsuarioService.java
│   │   └── AssociacaoService.java
│   ├── controller/
│   │   ├── OperadorController.java
│   │   ├── ProdutoController.java
│   │   ├── UsuarioController.java
│   │   ├── AssociacaoController.java
│   │   └── GlobalExceptionHandler.java
│   ├── dto/
│   │   ├── request/
│   │   └── response/
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

#### 🔍 Consultas Principais

**1. Buscar produtos associados a um operador**
```http
GET /api/operadores/{id}/produtos
```

**2. Buscar usuários associados a um produto**
```http
GET /api/produtos/{id}/usuarios
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
| GET | `/api/produtos/{id}/usuarios` | **Usuários do produto** |
| GET | `/api/produtos/categorias` | Lista categorias |

#### 👤 Usuários

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/usuarios` | Lista todos os usuários |
| GET | `/api/usuarios/{id}` | Busca usuário por ID |
| POST | `/api/usuarios` | Cria novo usuário |
| PUT | `/api/usuarios/{id}` | Atualiza usuário |
| DELETE | `/api/usuarios/{id}` | Remove usuário (soft delete) |
| GET | `/api/usuarios/{id}/produtos` | Produtos do usuário |

#### 🔗 Associações

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/associacoes/operador-produto` | Associa produto a operador |
| POST | `/api/associacoes/usuario-produto` | Associa produto a usuário |
| DELETE | `/api/associacoes/operador-produto/{opId}/{prodId}` | Remove associação |
| DELETE | `/api/associacoes/usuario-produto/{userId}/{prodId}` | Remove associação |

## Exemplos de Uso

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
    "categoria": "Eletrônicos"
  }
]
```

### 5. Buscar Usuários de um Produto

```http
GET /api/produtos/1/usuarios
```

**Resposta:**
```json
[
  {
    "id": 1,
    "nome": "Maria Santos",
    "email": "maria@email.com",
    "telefone": "11888888888"
  }
]
```

## Filtros e Paginação

### Filtros Disponíveis

**Operadores:**
- `?nome=João` - Busca por nome (parcial)

**Produtos:**
- `?nome=Smartphone` - Busca por nome (parcial)
- `?categoria=Eletrônicos` - Busca por categoria
- `?precoMin=100&precoMax=1000` - Busca por faixa de preço

**Usuários:**
- `?nome=Maria` - Busca por nome (parcial)

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

### Códigos de Status

- `200` - Sucesso
- `201` - Criado com sucesso
- `204` - Removido com sucesso
- `400` - Erro de validação ou regra de negócio
- `404` - Recurso não encontrado
- `500` - Erro interno do servidor


## Testes

O projeto inclui testes unitários e de integração:

### Executar Testes

```bash
# Todos os testes
mvn test

# Apenas testes unitários
mvn test -Dtest="*Test"

# Apenas testes de integração
mvn test -Dtest="*IT"

# Com relatório de cobertura
mvn test jacoco:report
```

### Estrutura de Testes

- **Testes de Service**: Testam a lógica de negócio com mocks
- **Testes de Repository**: Testam consultas JPA com banco em memória
- **Testes de Controller**: Testam endpoints REST (se implementados)

## Boas Práticas Implementadas

### 🏗️ Arquitetura

- **Separação de responsabilidades**: Cada camada tem sua responsabilidade específica
- **Injeção de dependência**: Uso do Spring para gerenciar dependências
- **DTOs**: Separação entre entidades de domínio e objetos de transferência
- **Mappers**: Conversão limpa entre entidades e DTOs

### 💾 Persistência

- **Soft Delete**: Registros são marcados como inativos ao invés de removidos
- **Auditoria**: Campos automáticos de data de criação e atualização
- **Índices**: Índices otimizados para consultas frequentes
- **Relacionamentos explícitos**: Tabelas de junção com campos adicionais

### 🔒 Validação

- **Bean Validation**: Validações declarativas nos DTOs
- **Validações de negócio**: Regras específicas nos services
- **Tratamento de exceções**: Handler global para padronizar erros

### 📊 Performance

- **Lazy Loading**: Carregamento sob demanda de relacionamentos
- **Paginação**: Evita carregar grandes volumes de dados
- **Consultas otimizadas**: Queries específicas para cada necessidade
- **Índices estratégicos**: Melhora performance das consultas

### 🧪 Qualidade

- **Testes unitários**: Cobertura das regras de negócio
- **Testes de integração**: Validação das consultas JPA
- **Mocks**: Isolamento de dependências nos testes
- **Assertions claras**: Testes legíveis e mantíveis

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

### Configurações Importantes

**application.properties:**
```properties
# JPA
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# H2 Console (apenas desenvolvimento)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Logging
logging.level.com.exemplo.consultaprodutos=DEBUG
logging.level.org.springframework.web=DEBUG
```

### Melhorias Futuras

1. **Segurança**: Implementar Spring Security com JWT
2. **Cache**: Adicionar Redis para cache de consultas frequentes
3. **Documentação**: Integrar Swagger/OpenAPI
4. **Monitoramento**: Adicionar Actuator e métricas
5. **Containerização**: Dockerfile e docker-compose
6. **CI/CD**: Pipeline de integração contínua

## Estrutura de Dados

### Diagrama ER

```
┌─────────────┐    ┌──────────────────┐    ┌─────────────┐
│   Operador  │────│ OperadorProduto  │────│   Produto   │
└─────────────┘    └──────────────────┘    └─────────────┘
                                                   │
                                           ┌──────────────────┐
                                           │ UsuarioProduto   │
                                           └──────────────────┘
                                                   │
                                           ┌─────────────┐
                                           │   Usuario   │
                                           └─────────────┘
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

**Usuario:**
- `nome`: Nome do usuário
- `email`: Email único
- `telefone`: Telefone de contato

## Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -am 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

## Licença

Este projeto está sob a licença MIT. Veja o arquivo LICENSE para mais detalhes.

## Contato

Para dúvidas ou sugestões, entre em contato através do email: exemplo@email.com

