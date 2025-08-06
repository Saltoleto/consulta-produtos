package com.exemplo.consultaprodutos.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidade de relacionamento entre Operador e Produto.
 * Implementa o relacionamento many-to-many com campos adicionais.
 */
@Entity
@Table(name = "operador_produto",
       indexes = {
           @Index(name = "idx_operador_produto_operador", columnList = "operador_id, ativo"),
           @Index(name = "idx_operador_produto_produto", columnList = "produto_id, ativo"),
           @Index(name = "idx_operador_produto_data", columnList = "data_associacao")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_operador_produto", columnNames = {"operador_id", "produto_id"})
       })
public class OperadorProduto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Operador é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operador_id", nullable = false)
    private Operador operador;

    @NotNull(message = "Produto é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(name = "data_associacao", nullable = false)
    private LocalDateTime dataAssociacao;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = Boolean.TRUE;

    // Construtor padrão
    public OperadorProduto() {
        this.dataAssociacao = LocalDateTime.now();
    }

    // Construtor com parâmetros
    public OperadorProduto(Operador operador, Produto produto) {
        this();
        this.operador = operador;
        this.produto = produto;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Operador getOperador() {
        return operador;
    }

    public void setOperador(Operador operador) {
        this.operador = operador;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public LocalDateTime getDataAssociacao() {
        return dataAssociacao;
    }

    public void setDataAssociacao(LocalDateTime dataAssociacao) {
        this.dataAssociacao = dataAssociacao;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    // Métodos de conveniência
    public void desativar() {
        this.ativo = Boolean.FALSE;
    }

    public void ativar() {
        this.ativo = Boolean.TRUE;
    }

    public boolean isAtivo() {
        return Boolean.TRUE.equals(this.ativo);
    }

    @PrePersist
    protected void onCreate() {
        if (dataAssociacao == null) {
            dataAssociacao = LocalDateTime.now();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OperadorProduto that = (OperadorProduto) o;
        return Objects.equals(operador, that.operador) && 
               Objects.equals(produto, that.produto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operador, produto);
    }

    @Override
    public String toString() {
        return "OperadorProduto{" +
                "id=" + id +
                ", operadorId=" + (operador != null ? operador.getId() : null) +
                ", produtoId=" + (produto != null ? produto.getId() : null) +
                ", dataAssociacao=" + dataAssociacao +
                ", ativo=" + ativo +
                '}';
    }
}

