package com.exemplo.consultaprodutos.service;

import com.exemplo.consultaprodutos.dto.request.AssociacaoRequestDTO;
import com.exemplo.consultaprodutos.entity.Operador;
import com.exemplo.consultaprodutos.entity.OperadorProduto;
import com.exemplo.consultaprodutos.entity.Produto;
import com.exemplo.consultaprodutos.repository.OperadorProdutoRepository;
import com.exemplo.consultaprodutos.repository.OperadorRepository;
import com.exemplo.consultaprodutos.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service para gerenciar associações entre entidades.
 */
@Service
@Transactional
public class AssociacaoService {

    private final OperadorRepository operadorRepository;
    private final ProdutoRepository produtoRepository;
    private final OperadorProdutoRepository operadorProdutoRepository;

    @Autowired
    public AssociacaoService(OperadorRepository operadorRepository,
                           ProdutoRepository produtoRepository,
                           OperadorProdutoRepository operadorProdutoRepository) {
        this.operadorRepository = operadorRepository;
        this.produtoRepository = produtoRepository;
        this.operadorProdutoRepository = operadorProdutoRepository;
    }

    /**
     * Associa um produto a um operador.
     */
    public void associarProdutoAOperador(AssociacaoRequestDTO requestDTO) {
        Long operadorId = requestDTO.getPrimeiraEntidadeId();
        Long produtoId = requestDTO.getSegundaEntidadeId();

        // Validar se operador existe
        Operador operador = operadorRepository.findById(operadorId)
                .orElseThrow(() -> new RuntimeException("Operador não encontrado com ID: " + operadorId));

        // Validar se produto existe
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + produtoId));

        // Verificar se associação já existe
        if (operadorProdutoRepository.existsByOperadorIdAndProdutoIdAndAtivoTrue(operadorId, produtoId)) {
            throw new RuntimeException("Associação entre operador e produto já existe");
        }

        // Criar nova associação
        OperadorProduto associacao = new OperadorProduto(operador, produto);
        operadorProdutoRepository.save(associacao);
    }

    /**
     * Remove associação entre operador e produto.
     */
    public void removerAssociacaoOperadorProduto(Long operadorId, Long produtoId) {
        OperadorProduto associacao = operadorProdutoRepository
                .findByOperadorIdAndProdutoIdAndAtivoTrue(operadorId, produtoId)
                .orElseThrow(() -> new RuntimeException("Associação não encontrada"));

        associacao.setAtivo(false);
        operadorProdutoRepository.save(associacao);
    }

    /**
     * Verifica se existe associação ativa entre operador e produto.
     */
    @Transactional(readOnly = true)
    public boolean existeAssociacaoOperadorProduto(Long operadorId, Long produtoId) {
        return operadorProdutoRepository.existsByOperadorIdAndProdutoIdAndAtivoTrue(operadorId, produtoId);
    }

    /**
     * Conta associações ativas de um operador.
     */
    @Transactional(readOnly = true)
    public long contarAssociacoesOperador(Long operadorId) {
        return operadorProdutoRepository.countByOperadorIdAndAtivoTrue(operadorId);
    }

    /**
     * Conta associações ativas de um produto (apenas operadores).
     */
    @Transactional(readOnly = true)
    public long contarAssociacoesProduto(Long produtoId) {
        return operadorProdutoRepository.countByProdutoIdAndAtivoTrue(produtoId);
    }
}

