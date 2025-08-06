package com.exemplo.consultaprodutos.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Entidade que representa um produto no sistema.
 * Um produto pode estar associado a múltiplos operadores.
 */
@Entity
@Table(name = "produto", 
       indexes = {
           @Index(name = "idx_produto_codigo", columnList = "codigo", unique = true),
           @Index(name = "idx_produto_categoria", columnList = "categoria"),
           @Index(name = "idx_produto_ativo", columnList = "ativo")
       })
public class Produto extends BaseEntity {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    @Column(name = "descricao", length = 500)
    private String descricao;

    @NotBlank(message = "Código é obrigatório")
    @Size(min = 3, max = 50, message = "Código deve ter entre 3 e 50 caracteres")
    @Column(name = "codigo", nullable = false, unique = true, length = 50)
    private String codigo;

    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Preço deve ser maior que zero")
    @Digits(integer = 10, fraction = 2, message = "Preço deve ter no máximo 10 dígitos inteiros e 2 decimais")
    @Column(name = "preco", nullable = false, precision = 12, scale = 2)
    private BigDecimal preco;

    @Size(max = 50, message = "Categoria deve ter no máximo 50 caracteres")
    @Column(name = "categoria", length = 50)
    private String categoria;

    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<OperadorProduto> operadorProdutos = new HashSet<>();

    // Construtor padrão
    public Produto() {
        super();
    }

    // Construtor com parâmetros essenciais
    public Produto(String nome, String codigo, BigDecimal preco) {
        this();
        this.nome = nome;
        this.codigo = codigo;
        this.preco = preco;
    }

    // Construtor completo
    public Produto(String nome, String descricao, String codigo, BigDecimal preco, String categoria) {
        this(nome, codigo, preco);
        this.descricao = descricao;
        this.categoria = categoria;
    }

    // Getters e Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Set<OperadorProduto> getOperadorProdutos() {
        return operadorProdutos;
    }

    public void setOperadorProdutos(Set<OperadorProduto> operadorProdutos) {
        this.operadorProdutos = operadorProdutos;
    }

    // Métodos de conveniência
    public void adicionarOperador(OperadorProduto operadorProduto) {
        operadorProdutos.add(operadorProduto);
        operadorProduto.setProduto(this);
    }

    public void removerOperador(OperadorProduto operadorProduto) {
        operadorProdutos.remove(operadorProduto);
        operadorProduto.setProduto(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Produto produto = (Produto) o;
        return Objects.equals(codigo, produto.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), codigo);
    }

    @Override
    public String toString() {
        return "Produto{" +
                "id=" + getId() +
                ", nome='" + nome + '\'' +
                ", codigo='" + codigo + '\'' +
                ", preco=" + preco +
                ", categoria='" + categoria + '\'' +
                ", ativo=" + getAtivo() +
                '}';
    }
}

