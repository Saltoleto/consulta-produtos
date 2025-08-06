package com.exemplo.consultaprodutos.mapper;

import com.exemplo.consultaprodutos.dto.request.ProdutoRequestDTO;
import com.exemplo.consultaprodutos.dto.response.ProdutoResponseDTO;
import com.exemplo.consultaprodutos.entity.Produto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para conversões entre Produto e Records.
 */
@Component
public class ProdutoMapper {

    /**
     * Converte ProdutoRequestDTO (Record) para entidade Produto.
     */
    public Produto toEntity(ProdutoRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        return new Produto(
            requestDTO.nome(),
            requestDTO.descricao(),
            requestDTO.codigo(),
            requestDTO.preco(),
            requestDTO.categoria()
        );
    }

    /**
     * Converte entidade Produto para ProdutoResponseDTO (Record).
     */
    public ProdutoResponseDTO toResponseDTO(Produto produto) {
        if (produto == null) {
            return null;
        }

        return new ProdutoResponseDTO(
            produto.getId(),
            produto.getNome(),
            produto.getDescricao(),
            produto.getCodigo(),
            produto.getPreco(),
            produto.getCategoria(),
            null, // totalOperadores será preenchido quando necessário
            produto.getDataCriacao(),
            produto.getDataAtualizacao()
        );
    }

    /**
     * Converte entidade Produto para ProdutoResponseDTO com total de operadores.
     */
    public ProdutoResponseDTO toResponseDTO(Produto produto, Long totalOperadores) {
        if (produto == null) {
            return null;
        }

        return new ProdutoResponseDTO(
            produto.getId(),
            produto.getNome(),
            produto.getDescricao(),
            produto.getCodigo(),
            produto.getPreco(),
            produto.getCategoria(),
            totalOperadores,
            produto.getDataCriacao(),
            produto.getDataAtualizacao()
        );
    }

    /**
     * Converte entidade Produto para ProdutoResponseDTO resumido (para listagens).
     */
    public ProdutoResponseDTO toResponseDTOResumo(Produto produto) {
        if (produto == null) {
            return null;
        }

        return new ProdutoResponseDTO(
            produto.getId(),
            produto.getNome(),
            produto.getCodigo(),
            produto.getPreco(),
            produto.getCategoria()
        );
    }

    /**
     * Converte lista de entidades Produto para lista de ProdutoResponseDTO.
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
     * Converte lista de entidades Produto para lista de ProdutoResponseDTO resumido.
     */
    public List<ProdutoResponseDTO> toResponseDTOResumoList(List<Produto> produtos) {
        if (produtos == null) {
            return null;
        }

        return produtos.stream()
                .map(this::toResponseDTOResumo)
                .collect(Collectors.toList());
    }

    /**
     * Atualiza entidade Produto com dados do ProdutoRequestDTO.
     */
    public void updateEntityFromDTO(Produto produto, ProdutoRequestDTO requestDTO) {
        if (produto == null || requestDTO == null) {
            return;
        }

        produto.setNome(requestDTO.nome());
        produto.setDescricao(requestDTO.descricao());
        produto.setCodigo(requestDTO.codigo());
        produto.setPreco(requestDTO.preco());
        produto.setCategoria(requestDTO.categoria());
    }
}

