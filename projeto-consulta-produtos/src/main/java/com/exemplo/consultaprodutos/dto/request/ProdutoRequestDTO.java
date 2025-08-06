package com.exemplo.consultaprodutos.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * Record para dados de entrada de Produto.
 */
public record ProdutoRequestDTO(
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    String nome,
    
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    String descricao,
    
    @NotBlank(message = "Código é obrigatório")
    @Size(min = 3, max = 50, message = "Código deve ter entre 3 e 50 caracteres")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Código deve conter apenas letras maiúsculas e números")
    String codigo,
    
    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    @Digits(integer = 10, fraction = 2, message = "Preço deve ter no máximo 10 dígitos inteiros e 2 decimais")
    BigDecimal preco,
    
    @Size(max = 50, message = "Categoria deve ter no máximo 50 caracteres")
    String categoria
) {
    
    /**
     * Construtor compacto para validações e normalizações adicionais.
     */
    public ProdutoRequestDTO {
        // Normalizar nome
        if (nome != null) {
            nome = nome.trim();
        }
        
        // Normalizar descrição
        if (descricao != null) {
            descricao = descricao.trim();
            if (descricao.isEmpty()) {
                descricao = null;
            }
        }
        
        // Normalizar código para uppercase
        if (codigo != null) {
            codigo = codigo.trim().toUpperCase();
        }
        
        // Normalizar categoria
        if (categoria != null) {
            categoria = categoria.trim();
            if (categoria.isEmpty()) {
                categoria = null;
            }
        }
    }
}

