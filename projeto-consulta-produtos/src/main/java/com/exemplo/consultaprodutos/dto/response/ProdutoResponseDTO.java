package com.exemplo.consultaprodutos.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de resposta para a entidade Produto.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProdutoResponseDTO {

    private Long id;
    private String nome;
    private String descricao;
    private String codigo;
    private BigDecimal preco;
    private String categoria;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataCriacao;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataAtualizacao;
    
    private Boolean ativo;
    private Long totalOperadores;
    private Long totalUsuarios;
    private List<OperadorResponseDTO> operadores;
    private List<UsuarioResponseDTO> usuarios;

    // Construtor padrão
    public ProdutoResponseDTO() {}

    // Construtor básico
    public ProdutoResponseDTO(Long id, String nome, String codigo, BigDecimal preco) {
        this.id = id;
        this.nome = nome;
        this.codigo = codigo;
        this.preco = preco;
    }

    // Construtor completo
    public ProdutoResponseDTO(Long id, String nome, String descricao, String codigo, 
                             BigDecimal preco, String categoria, LocalDateTime dataCriacao, 
                             LocalDateTime dataAtualizacao, Boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.codigo = codigo;
        this.preco = preco;
        this.categoria = categoria;
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

    public Long getTotalOperadores() {
        return totalOperadores;
    }

    public void setTotalOperadores(Long totalOperadores) {
        this.totalOperadores = totalOperadores;
    }

    public Long getTotalUsuarios() {
        return totalUsuarios;
    }

    public void setTotalUsuarios(Long totalUsuarios) {
        this.totalUsuarios = totalUsuarios;
    }

    public List<OperadorResponseDTO> getOperadores() {
        return operadores;
    }

    public void setOperadores(List<OperadorResponseDTO> operadores) {
        this.operadores = operadores;
    }

    public List<UsuarioResponseDTO> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<UsuarioResponseDTO> usuarios) {
        this.usuarios = usuarios;
    }

    @Override
    public String toString() {
        return "ProdutoResponseDTO{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", codigo='" + codigo + '\'' +
                ", preco=" + preco +
                ", categoria='" + categoria + '\'' +
                ", ativo=" + ativo +
                ", totalOperadores=" + totalOperadores +
                ", totalUsuarios=" + totalUsuarios +
                '}';
    }
}

