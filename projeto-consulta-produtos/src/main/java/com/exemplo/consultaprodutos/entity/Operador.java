package com.exemplo.consultaprodutos.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Entidade que representa um operador no sistema.
 * Um operador pode ter múltiplos produtos associados.
 */
@Entity
@Table(name = "operador", 
       indexes = {
           @Index(name = "idx_operador_email", columnList = "email", unique = true),
           @Index(name = "idx_operador_ativo", columnList = "ativo")
       })
public class Operador extends BaseEntity {

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

    @OneToMany(mappedBy = "operador", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<OperadorProduto> operadorProdutos = new HashSet<>();

    // Construtor padrão
    public Operador() {
        super();
    }

    // Construtor com parâmetros essenciais
    public Operador(String nome, String email) {
        this();
        this.nome = nome;
        this.email = email;
    }

    // Construtor completo
    public Operador(String nome, String email, String telefone) {
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

    public Set<OperadorProduto> getOperadorProdutos() {
        return operadorProdutos;
    }

    public void setOperadorProdutos(Set<OperadorProduto> operadorProdutos) {
        this.operadorProdutos = operadorProdutos;
    }

    // Métodos de conveniência
    public void adicionarProduto(OperadorProduto operadorProduto) {
        operadorProdutos.add(operadorProduto);
        operadorProduto.setOperador(this);
    }

    public void removerProduto(OperadorProduto operadorProduto) {
        operadorProdutos.remove(operadorProduto);
        operadorProduto.setOperador(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Operador operador = (Operador) o;
        return Objects.equals(email, operador.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), email);
    }

    @Override
    public String toString() {
        return "Operador{" +
                "id=" + getId() +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                ", ativo=" + getAtivo() +
                '}';
    }
}

