package com.exemplo.consultaprodutos.mapper;

import com.exemplo.consultaprodutos.dto.request.OperadorRequestDTO;
import com.exemplo.consultaprodutos.dto.response.OperadorResponseDTO;
import com.exemplo.consultaprodutos.entity.Operador;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para conversões entre Operador e Records.
 */
@Component
public class OperadorMapper {

    /**
     * Converte OperadorRequestDTO (Record) para entidade Operador.
     */
    public Operador toEntity(OperadorRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        return new Operador(
            requestDTO.nome(),
            requestDTO.email(),
            requestDTO.telefone()
        );
    }

    /**
     * Converte entidade Operador para OperadorResponseDTO (Record).
     */
    public OperadorResponseDTO toResponseDTO(Operador operador) {
        if (operador == null) {
            return null;
        }

        return new OperadorResponseDTO(
            operador.getId(),
            operador.getNome(),
            operador.getEmail(),
            operador.getTelefone(),
            null, // totalProdutos será preenchido quando necessário
            operador.getDataCriacao(),
            operador.getDataAtualizacao()
        );
    }

    /**
     * Converte entidade Operador para OperadorResponseDTO com total de produtos.
     */
    public OperadorResponseDTO toResponseDTO(Operador operador, Long totalProdutos) {
        if (operador == null) {
            return null;
        }

        return new OperadorResponseDTO(
            operador.getId(),
            operador.getNome(),
            operador.getEmail(),
            operador.getTelefone(),
            totalProdutos,
            operador.getDataCriacao(),
            operador.getDataAtualizacao()
        );
    }

    /**
     * Converte entidade Operador para OperadorResponseDTO resumido (para listagens).
     */
    public OperadorResponseDTO toResponseDTOResumo(Operador operador) {
        if (operador == null) {
            return null;
        }

        return new OperadorResponseDTO(
            operador.getId(),
            operador.getNome(),
            operador.getEmail()
        );
    }

    /**
     * Converte lista de entidades Operador para lista de OperadorResponseDTO.
     */
    public List<OperadorResponseDTO> toResponseDTOList(List<Operador> operadores) {
        if (operadores == null) {
            return null;
        }

        return operadores.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Converte lista de entidades Operador para lista de OperadorResponseDTO resumido.
     */
    public List<OperadorResponseDTO> toResponseDTOResumoList(List<Operador> operadores) {
        if (operadores == null) {
            return null;
        }

        return operadores.stream()
                .map(this::toResponseDTOResumo)
                .collect(Collectors.toList());
    }

    /**
     * Atualiza entidade Operador com dados do OperadorRequestDTO.
     */
    public void updateEntityFromDTO(Operador operador, OperadorRequestDTO requestDTO) {
        if (operador == null || requestDTO == null) {
            return;
        }

        operador.setNome(requestDTO.nome());
        operador.setEmail(requestDTO.email());
        operador.setTelefone(requestDTO.telefone());
    }
}

