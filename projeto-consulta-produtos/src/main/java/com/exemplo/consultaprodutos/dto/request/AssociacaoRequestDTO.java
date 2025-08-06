package com.exemplo.consultaprodutos.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * DTO de requisição para criação de associações entre entidades.
 */
public class AssociacaoRequestDTO {

    @NotNull(message = "ID da primeira entidade é obrigatório")
    private Long primeiraEntidadeId;

    @NotNull(message = "ID da segunda entidade é obrigatório")
    private Long segundaEntidadeId;

    // Construtor padrão
    public AssociacaoRequestDTO() {}

    // Construtor com parâmetros
    public AssociacaoRequestDTO(Long primeiraEntidadeId, Long segundaEntidadeId) {
        this.primeiraEntidadeId = primeiraEntidadeId;
        this.segundaEntidadeId = segundaEntidadeId;
    }

    // Getters e Setters
    public Long getPrimeiraEntidadeId() {
        return primeiraEntidadeId;
    }

    public void setPrimeiraEntidadeId(Long primeiraEntidadeId) {
        this.primeiraEntidadeId = primeiraEntidadeId;
    }

    public Long getSegundaEntidadeId() {
        return segundaEntidadeId;
    }

    public void setSegundaEntidadeId(Long segundaEntidadeId) {
        this.segundaEntidadeId = segundaEntidadeId;
    }

    @Override
    public String toString() {
        return "AssociacaoRequestDTO{" +
                "primeiraEntidadeId=" + primeiraEntidadeId +
                ", segundaEntidadeId=" + segundaEntidadeId +
                '}';
    }
}

