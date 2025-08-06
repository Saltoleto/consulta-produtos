package com.exemplo.consultaprodutos.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * Record para dados de saída de Operador.
 */
public record OperadorResponseDTO(
    Long id,
    String nome,
    String email,
    String telefone,
    Long totalProdutos,
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime dataCriacao,
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime dataAtualizacao
) {
    
    /**
     * Construtor simplificado para listagens resumidas.
     */
    public OperadorResponseDTO(Long id, String nome, String email) {
        this(id, nome, email, null, null, null, null);
    }
    
    /**
     * Construtor com total de produtos.
     */
    public OperadorResponseDTO(Long id, String nome, String email, String telefone, Long totalProdutos) {
        this(id, nome, email, telefone, totalProdutos, null, null);
    }
}

