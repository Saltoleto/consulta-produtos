package com.exemplo.consultaprodutos.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Record para dados de entrada de Operador.
 */
public record OperadorRequestDTO(
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    String nome,
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter formato válido")
    @Size(max = 150, message = "Email deve ter no máximo 150 caracteres")
    String email,
    
    @Pattern(regexp = "\\d{10,11}", message = "Telefone deve ter 10 ou 11 dígitos")
    String telefone
) {
    
    /**
     * Construtor compacto para validações adicionais.
     */
    public OperadorRequestDTO {
        // Normalizar telefone removendo caracteres especiais
        if (telefone != null) {
            telefone = telefone.replaceAll("[^\\d]", "");
        }
        
        // Normalizar email para lowercase
        if (email != null) {
            email = email.toLowerCase().trim();
        }
        
        // Normalizar nome
        if (nome != null) {
            nome = nome.trim();
        }
    }
}

