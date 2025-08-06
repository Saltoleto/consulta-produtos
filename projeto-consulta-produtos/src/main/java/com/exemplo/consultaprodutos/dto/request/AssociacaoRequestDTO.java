package com.exemplo.consultaprodutos.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Record para dados de entrada de Associação entre entidades.
 */
public record AssociacaoRequestDTO(
    
    @NotNull(message = "ID da primeira entidade é obrigatório")
    @Positive(message = "ID da primeira entidade deve ser positivo")
    Long primeiraEntidadeId,
    
    @NotNull(message = "ID da segunda entidade é obrigatório")
    @Positive(message = "ID da segunda entidade deve ser positivo")
    Long segundaEntidadeId
) {
    
    /**
     * Construtor compacto para validações adicionais.
     */
    public AssociacaoRequestDTO {
        // Validar que os IDs são diferentes
        if (primeiraEntidadeId != null && segundaEntidadeId != null && 
            primeiraEntidadeId.equals(segundaEntidadeId)) {
            throw new IllegalArgumentException("Os IDs das entidades devem ser diferentes");
        }
    }
}

