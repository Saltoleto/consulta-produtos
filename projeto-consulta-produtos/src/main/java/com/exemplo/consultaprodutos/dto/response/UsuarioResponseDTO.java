package com.exemplo.consultaprodutos.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de resposta para a entidade Usuario.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsuarioResponseDTO {

    private Long id;
    private String nome;
    private String email;
    private String telefone;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataCriacao;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataAtualizacao;
    
    private Boolean ativo;
    private Long totalProdutos;
    private List<ProdutoResponseDTO> produtos;

    // Construtor padrão
    public UsuarioResponseDTO() {}

    // Construtor básico
    public UsuarioResponseDTO(Long id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
    }

    // Construtor completo
    public UsuarioResponseDTO(Long id, String nome, String email, String telefone, 
                             LocalDateTime dataCriacao, LocalDateTime dataAtualizacao, Boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
        this.ativo = ativo;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Long getTotalProdutos() {
        return totalProdutos;
    }

    public void setTotalProdutos(Long totalProdutos) {
        this.totalProdutos = totalProdutos;
    }

    public List<ProdutoResponseDTO> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<ProdutoResponseDTO> produtos) {
        this.produtos = produtos;
    }

    @Override
    public String toString() {
        return "UsuarioResponseDTO{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                ", ativo=" + ativo +
                ", totalProdutos=" + totalProdutos +
                '}';
    }
}

