# Análise e Design da Estrutura de Dados

## Visão Geral

O sistema implementa um modelo de relacionamento many-to-many entre três entidades principais: **Operador**, **Produto** e **Usuário**. O design permite consultas eficientes para:

1. Produtos associados a um operador
2. Usuários associados a um produto

## Entidades Principais

### 1. Operador
Representa os operadores do sistema que podem ter produtos associados.

**Campos:**
- `id`: Identificador único (Long)
- `nome`: Nome do operador (String)
- `email`: Email do operador (String)
- `telefone`: Telefone de contato (String)
- `dataCriacao`: Data de criação do registro (LocalDateTime)
- `dataAtualizacao`: Data da última atualização (LocalDateTime)
- `ativo`: Flag para soft delete (Boolean)

### 2. Produto
Representa os produtos que podem ser associados a operadores e usuários.

**Campos:**
- `id`: Identificador único (Long)
- `nome`: Nome do produto (String)
- `descricao`: Descrição detalhada (String)
- `codigo`: Código único do produto (String)
- `preco`: Preço do produto (BigDecimal)
- `categoria`: Categoria do produto (String)
- `dataCriacao`: Data de criação do registro (LocalDateTime)
- `dataAtualizacao`: Data da última atualização (LocalDateTime)
- `ativo`: Flag para soft delete (Boolean)

### 3. Usuario
Representa os usuários que podem ter produtos associados.

**Campos:**
- `id`: Identificador único (Long)
- `nome`: Nome do usuário (String)
- `email`: Email do usuário (String)
- `telefone`: Telefone de contato (String)
- `dataCriacao`: Data de criação do registro (LocalDateTime)
- `dataAtualizacao`: Data da última atualização (LocalDateTime)
- `ativo`: Flag para soft delete (Boolean)

## Tabelas de Relacionamento

### 1. OperadorProduto
Tabela de junção para relacionamento many-to-many entre Operador e Produto.

**Campos:**
- `id`: Identificador único (Long)
- `operadorId`: Referência ao operador (Long)
- `produtoId`: Referência ao produto (Long)
- `dataAssociacao`: Data da associação (LocalDateTime)
- `ativo`: Flag para soft delete (Boolean)

### 2. UsuarioProduto
Tabela de junção para relacionamento many-to-many entre Usuario e Produto.

**Campos:**
- `id`: Identificador único (Long)
- `usuarioId`: Referência ao usuário (Long)
- `produtoId`: Referência ao produto (Long)
- `dataAssociacao`: Data da associação (LocalDateTime)
- `ativo`: Flag para soft delete (Boolean)

## Decisões de Design

### 1. Soft Delete
Todas as entidades implementam soft delete através do campo `ativo`, permitindo manter histórico e integridade referencial.

### 2. Auditoria
Campos de `dataCriacao` e `dataAtualizacao` em todas as entidades para rastreabilidade.

### 3. Relacionamentos Explícitos
Uso de tabelas de junção explícitas ao invés de anotações JPA diretas para maior controle e flexibilidade.

### 4. Identificadores Únicos
Uso de `Long` para IDs, permitindo grande volume de dados.

### 5. Precisão Monetária
Uso de `BigDecimal` para valores monetários, evitando problemas de precisão.

## Consultas Principais

### 1. Produtos por Operador
```sql
SELECT p.* FROM produto p
INNER JOIN operador_produto op ON p.id = op.produto_id
WHERE op.operador_id = ? AND op.ativo = true AND p.ativo = true
```

### 2. Usuários por Produto
```sql
SELECT u.* FROM usuario u
INNER JOIN usuario_produto up ON u.id = up.usuario_id
WHERE up.produto_id = ? AND up.ativo = true AND u.ativo = true
```

## Índices Recomendados

1. `operador_produto(operador_id, ativo)`
2. `operador_produto(produto_id, ativo)`
3. `usuario_produto(usuario_id, ativo)`
4. `usuario_produto(produto_id, ativo)`
5. `produto(codigo)` - único
6. `operador(email)` - único
7. `usuario(email)` - único

