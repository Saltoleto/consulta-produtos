package com.exemplo.consultaprodutos.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Record para dados de saída de Produto.
 */
public record ProdutoResponseDTO(
    Long id,
    String nome,
    String descricao,
    String codigo,
    BigDecimal preco,
    String categoria,
    Long totalOperadores,
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime dataCriacao,
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime dataAtualizacao
) {
    
    /**
     * Construtor simplificado para listagens resumidas.
     */
    public ProdutoResponseDTO(Long id, String nome, String codigo, BigDecimal preco) {
        this(id, nome, null, codigo, preco, null, null, null, null);
    }
    
    /**
     * Construtor com categoria.
     */
    public ProdutoResponseDTO(Long id, String nome, String codigo, BigDecimal preco, String categoria) {
        this(id, nome, null, codigo, preco, categoria, null, null, null);
    }
    
    /**
     * Construtor com total de operadores.
     */
    public ProdutoResponseDTO(Long id, String nome, String descricao, String codigo, 
                             BigDecimal preco, String categoria, Long totalOperadores) {
        this(id, nome, descricao, codigo, preco, categoria, totalOperadores, null, null);
    }
}

