package com.exemplo.consultaprodutos.mapper;

import com.exemplo.consultaprodutos.dto.request.ProdutoRequestDTO;
import com.exemplo.consultaprodutos.dto.response.ProdutoResponseDTO;
import com.exemplo.consultaprodutos.entity.Produto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para conversões entre Produto e seus DTOs.
 */
@Component
public class ProdutoMapper {

    /**
     * Converte ProdutoRequestDTO para Produto (para criação).
     */
    public Produto toEntity(ProdutoRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Produto produto = new Produto();
        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setCodigo(dto.getCodigo());
        produto.setPreco(dto.getPreco());
        produto.setCategoria(dto.getCategoria());
        
        return produto;
    }

    /**
     * Atualiza entidade Produto com dados do ProdutoRequestDTO.
     */
    public void updateEntity(Produto produto, ProdutoRequestDTO dto) {
        if (produto == null || dto == null) {
            return;
        }

        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setCodigo(dto.getCodigo());
        produto.setPreco(dto.getPreco());
        produto.setCategoria(dto.getCategoria());
    }

    /**
     * Converte Produto para ProdutoResponseDTO (básico).
     */
    public ProdutoResponseDTO toResponseDTO(Produto produto) {
        if (produto == null) {
            return null;
        }

        ProdutoResponseDTO dto = new ProdutoResponseDTO();
        dto.setId(produto.getId());
        dto.setNome(produto.getNome());
        dto.setDescricao(produto.getDescricao());
        dto.setCodigo(produto.getCodigo());
        dto.setPreco(produto.getPreco());
        dto.setCategoria(produto.getCategoria());
        dto.setDataCriacao(produto.getDataCriacao());
        dto.setDataAtualizacao(produto.getDataAtualizacao());
        dto.setAtivo(produto.getAtivo());
        
        return dto;
    }

    /**
     * Converte Produto para ProdutoResponseDTO (com totais de associações).
     */
    public ProdutoResponseDTO toResponseDTO(Produto produto, Long totalOperadores, Long totalUsuarios) {
        ProdutoResponseDTO dto = toResponseDTO(produto);
        if (dto != null) {
            dto.setTotalOperadores(totalOperadores);
            dto.setTotalUsuarios(totalUsuarios);
        }
        return dto;
    }

    /**
     * Converte Produto para ProdutoResponseDTO (resumido, sem dados de auditoria).
     */
    public ProdutoResponseDTO toResponseDTOResumo(Produto produto) {
        if (produto == null) {
            return null;
        }

        ProdutoResponseDTO dto = new ProdutoResponseDTO();
        dto.setId(produto.getId());
        dto.setNome(produto.getNome());
        dto.setDescricao(produto.getDescricao());
        dto.setCodigo(produto.getCodigo());
        dto.setPreco(produto.getPreco());
        dto.setCategoria(produto.getCategoria());
        
        return dto;
    }

    /**
     * Converte lista de Produto para lista de ProdutoResponseDTO.
     */
    public List<ProdutoResponseDTO> toResponseDTOList(List<Produto> produtos) {
        if (produtos == null) {
            return null;
        }

        return produtos.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Converte lista de Produto para lista de ProdutoResponseDTO (resumido).
     */
    public List<ProdutoResponseDTO> toResponseDTOResumoList(List<Produto> produtos) {
        if (produtos == null) {
            return null;
        }

        return produtos.stream()
                .map(this::toResponseDTOResumo)
                .collect(Collectors.toList());
    }
}

