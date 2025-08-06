package com.exemplo.consultaprodutos.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidade de relacionamento entre Usuario e Produto.
 * Implementa o relacionamento many-to-many com campos adicionais.
 */
@Entity
@Table(name = "usuario_produto",
       indexes = {
           @Index(name = "idx_usuario_produto_usuario", columnList = "usuario_id, ativo"),
           @Index(name = "idx_usuario_produto_produto", columnList = "produto_id, ativo"),
           @Index(name = "idx_usuario_produto_data", columnList = "data_associacao")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_usuario_produto", columnNames = {"usuario_id", "produto_id"})
       })
public class UsuarioProduto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Usuário é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotNull(message = "Produto é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(name = "data_associacao", nullable = false)
    private LocalDateTime dataAssociacao;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = Boolean.TRUE;

    // Construtor padrão
    public UsuarioProduto() {
        this.dataAssociacao = LocalDateTime.now();
    }

    // Construtor com parâmetros
    public UsuarioProduto(Usuario usuario, Produto produto) {
        this();
        this.usuario = usuario;
        this.produto = produto;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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
        UsuarioProduto that = (UsuarioProduto) o;
        return Objects.equals(usuario, that.usuario) && 
               Objects.equals(produto, that.produto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuario, produto);
    }

    @Override
    public String toString() {
        return "UsuarioProduto{" +
                "id=" + id +
                ", usuarioId=" + (usuario != null ? usuario.getId() : null) +
                ", produtoId=" + (produto != null ? produto.getId() : null) +
                ", dataAssociacao=" + dataAssociacao +
                ", ativo=" + ativo +
                '}';
    }
}

