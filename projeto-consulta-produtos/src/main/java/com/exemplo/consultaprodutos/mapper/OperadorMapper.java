package com.exemplo.consultaprodutos.mapper;

import com.exemplo.consultaprodutos.dto.request.OperadorRequestDTO;
import com.exemplo.consultaprodutos.dto.response.OperadorResponseDTO;
import com.exemplo.consultaprodutos.entity.Operador;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para conversões entre Operador e seus DTOs.
 */
@Component
public class OperadorMapper {

    /**
     * Converte OperadorRequestDTO para Operador (para criação).
     */
    public Operador toEntity(OperadorRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Operador operador = new Operador();
        operador.setNome(dto.getNome());
        operador.setEmail(dto.getEmail());
        operador.setTelefone(dto.getTelefone());
        
        return operador;
    }

    /**
     * Atualiza entidade Operador com dados do OperadorRequestDTO.
     */
    public void updateEntity(Operador operador, OperadorRequestDTO dto) {
        if (operador == null || dto == null) {
            return;
        }

        operador.setNome(dto.getNome());
        operador.setEmail(dto.getEmail());
        operador.setTelefone(dto.getTelefone());
    }

    /**
     * Converte Operador para OperadorResponseDTO (básico).
     */
    public OperadorResponseDTO toResponseDTO(Operador operador) {
        if (operador == null) {
            return null;
        }

        OperadorResponseDTO dto = new OperadorResponseDTO();
        dto.setId(operador.getId());
        dto.setNome(operador.getNome());
        dto.setEmail(operador.getEmail());
        dto.setTelefone(operador.getTelefone());
        dto.setDataCriacao(operador.getDataCriacao());
        dto.setDataAtualizacao(operador.getDataAtualizacao());
        dto.setAtivo(operador.getAtivo());
        
        return dto;
    }

    /**
     * Converte Operador para OperadorResponseDTO (com total de produtos).
     */
    public OperadorResponseDTO toResponseDTO(Operador operador, Long totalProdutos) {
        OperadorResponseDTO dto = toResponseDTO(operador);
        if (dto != null) {
            dto.setTotalProdutos(totalProdutos);
        }
        return dto;
    }

    /**
     * Converte Operador para OperadorResponseDTO (resumido, sem dados de auditoria).
     */
    public OperadorResponseDTO toResponseDTOResumo(Operador operador) {
        if (operador == null) {
            return null;
        }

        OperadorResponseDTO dto = new OperadorResponseDTO();
        dto.setId(operador.getId());
        dto.setNome(operador.getNome());
        dto.setEmail(operador.getEmail());
        dto.setTelefone(operador.getTelefone());
        
        return dto;
    }

    /**
     * Converte lista de Operador para lista de OperadorResponseDTO.
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
     * Converte lista de Operador para lista de OperadorResponseDTO (resumido).
     */
    public List<OperadorResponseDTO> toResponseDTOResumoList(List<Operador> operadores) {
        if (operadores == null) {
            return null;
        }

        return operadores.stream()
                .map(this::toResponseDTOResumo)
                .collect(Collectors.toList());
    }
}

