package com.exemplo.consultaprodutos.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * DTO de requisição para criação e atualização de Produto.
 */
public class ProdutoRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nome;

    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    private String descricao;

    @NotBlank(message = "Código é obrigatório")
    @Size(min = 3, max = 50, message = "Código deve ter entre 3 e 50 caracteres")
    private String codigo;

    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Preço deve ser maior que zero")
    @Digits(integer = 10, fraction = 2, message = "Preço deve ter no máximo 10 dígitos inteiros e 2 decimais")
    private BigDecimal preco;

    @Size(max = 50, message = "Categoria deve ter no máximo 50 caracteres")
    private String categoria;

    // Construtor padrão
    public ProdutoRequestDTO() {}

    // Construtor com parâmetros essenciais
    public ProdutoRequestDTO(String nome, String codigo, BigDecimal preco) {
        this.nome = nome;
        this.codigo = codigo;
        this.preco = preco;
    }

    // Construtor completo
    public ProdutoRequestDTO(String nome, String descricao, String codigo, BigDecimal preco, String categoria) {
        this.nome = nome;
        this.descricao = descricao;
        this.codigo = codigo;
        this.preco = preco;
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

    @Override
    public String toString() {
        return "ProdutoRequestDTO{" +
                "nome='" + nome + '\'' +
                ", codigo='" + codigo + '\'' +
                ", preco=" + preco +
                ", categoria='" + categoria + '\'' +
                '}';
    }
}

