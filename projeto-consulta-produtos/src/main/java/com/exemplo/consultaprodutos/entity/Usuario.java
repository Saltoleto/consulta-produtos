package com.exemplo.consultaprodutos.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Entidade que representa um usuário no sistema.
 * Um usuário pode ter múltiplos produtos associados.
 */
@Entity
@Table(name = "usuario", 
       indexes = {
           @Index(name = "idx_usuario_email", columnList = "email", unique = true),
           @Index(name = "idx_usuario_ativo", columnList = "ativo")
       })
public class Usuario extends BaseEntity {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter formato válido")
    @Size(max = 150, message = "Email deve ter no máximo 150 caracteres")
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    @Column(name = "telefone", length = 20)
    private String telefone;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UsuarioProduto> usuarioProdutos = new HashSet<>();

    // Construtor padrão
    public Usuario() {
        super();
    }

    // Construtor com parâmetros essenciais
    public Usuario(String nome, String email) {
        this();
        this.nome = nome;
        this.email = email;
    }

    // Construtor completo
    public Usuario(String nome, String email, String telefone) {
        this(nome, email);
        this.telefone = telefone;
    }

    // Getters e Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Set<UsuarioProduto> getUsuarioProdutos() {
        return usuarioProdutos;
    }

    public void setUsuarioProdutos(Set<UsuarioProduto> usuarioProdutos) {
        this.usuarioProdutos = usuarioProdutos;
    }

    // Métodos de conveniência
    public void adicionarProduto(UsuarioProduto usuarioProduto) {
        usuarioProdutos.add(usuarioProduto);
        usuarioProduto.setUsuario(this);
    }

    public void removerProduto(UsuarioProduto usuarioProduto) {
        usuarioProdutos.remove(usuarioProduto);
        usuarioProduto.setUsuario(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(email, usuario.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), email);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + getId() +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                ", ativo=" + getAtivo() +
                '}';
    }
}

