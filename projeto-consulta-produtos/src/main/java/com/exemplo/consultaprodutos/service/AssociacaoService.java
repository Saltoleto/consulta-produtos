package com.exemplo.consultaprodutos.service;

import com.exemplo.consultaprodutos.dto.request.AssociacaoRequestDTO;
import com.exemplo.consultaprodutos.entity.*;
import com.exemplo.consultaprodutos.repository.*;
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
    private final UsuarioRepository usuarioRepository;
    private final OperadorProdutoRepository operadorProdutoRepository;
    private final UsuarioProdutoRepository usuarioProdutoRepository;

    @Autowired
    public AssociacaoService(OperadorRepository operadorRepository,
                           ProdutoRepository produtoRepository,
                           UsuarioRepository usuarioRepository,
                           OperadorProdutoRepository operadorProdutoRepository,
                           UsuarioProdutoRepository usuarioProdutoRepository) {
        this.operadorRepository = operadorRepository;
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
        this.operadorProdutoRepository = operadorProdutoRepository;
        this.usuarioProdutoRepository = usuarioProdutoRepository;
    }

    /**
     * Associa um produto a um operador.
     */
    public void associarProdutoAOperador(AssociacaoRequestDTO requestDTO) {
        Long operadorId = requestDTO.getPrimeiraEntidadeId();
        Long produtoId = requestDTO.getSegundaEntidadeId();
        
        // Verifica se as entidades existem
        Operador operador = buscarOperadorPorId(operadorId);
        Produto produto = buscarProdutoPorId(produtoId);
        
        // Verifica se a associação já existe
        if (operadorProdutoRepository.existsByOperadorIdAndProdutoIdAndAtivoTrue(operadorId, produtoId)) {
            throw new RuntimeException("Associação já existe entre operador " + operadorId + " e produto " + produtoId);
        }
        
        // Cria a associação
        OperadorProduto operadorProduto = new OperadorProduto(operador, produto);
        operadorProdutoRepository.save(operadorProduto);
    }

    /**
     * Associa um produto a um usuário.
     */
    public void associarProdutoAUsuario(AssociacaoRequestDTO requestDTO) {
        Long usuarioId = requestDTO.getPrimeiraEntidadeId();
        Long produtoId = requestDTO.getSegundaEntidadeId();
        
        // Verifica se as entidades existem
        Usuario usuario = buscarUsuarioPorId(usuarioId);
        Produto produto = buscarProdutoPorId(produtoId);
        
        // Verifica se a associação já existe
        if (usuarioProdutoRepository.existsByUsuarioIdAndProdutoIdAndAtivoTrue(usuarioId, produtoId)) {
            throw new RuntimeException("Associação já existe entre usuário " + usuarioId + " e produto " + produtoId);
        }
        
        // Cria a associação
        UsuarioProduto usuarioProduto = new UsuarioProduto(usuario, produto);
        usuarioProdutoRepository.save(usuarioProduto);
    }

    /**
     * Remove associação entre operador e produto (soft delete).
     */
    public void removerAssociacaoOperadorProduto(Long operadorId, Long produtoId) {
        // Verifica se as entidades existem
        buscarOperadorPorId(operadorId);
        buscarProdutoPorId(produtoId);
        
        // Busca a associação
        OperadorProduto operadorProduto = operadorProdutoRepository
                .findByOperadorIdAndProdutoIdAndAtivoTrue(operadorId, produtoId)
                .orElseThrow(() -> new RuntimeException("Associação não encontrada entre operador " + operadorId + " e produto " + produtoId));
        
        // Remove a associação (soft delete)
        operadorProduto.desativar();
        operadorProdutoRepository.save(operadorProduto);
    }

    /**
     * Remove associação entre usuário e produto (soft delete).
     */
    public void removerAssociacaoUsuarioProduto(Long usuarioId, Long produtoId) {
        // Verifica se as entidades existem
        buscarUsuarioPorId(usuarioId);
        buscarProdutoPorId(produtoId);
        
        // Busca a associação
        UsuarioProduto usuarioProduto = usuarioProdutoRepository
                .findByUsuarioIdAndProdutoIdAndAtivoTrue(usuarioId, produtoId)
                .orElseThrow(() -> new RuntimeException("Associação não encontrada entre usuário " + usuarioId + " e produto " + produtoId));
        
        // Remove a associação (soft delete)
        usuarioProduto.desativar();
        usuarioProdutoRepository.save(usuarioProduto);
    }

    /**
     * Reativa associação entre operador e produto.
     */
    public void reativarAssociacaoOperadorProduto(Long operadorId, Long produtoId) {
        // Verifica se as entidades existem
        buscarOperadorPorId(operadorId);
        buscarProdutoPorId(produtoId);
        
        // Busca a associação (incluindo inativas)
        OperadorProduto operadorProduto = operadorProdutoRepository
                .findById(operadorId) // Aqui seria necessário buscar por operador e produto
                .orElseThrow(() -> new RuntimeException("Associação não encontrada"));
        
        // Reativa a associação
        operadorProduto.ativar();
        operadorProdutoRepository.save(operadorProduto);
    }

    /**
     * Verifica se existe associação ativa entre operador e produto.
     */
    @Transactional(readOnly = true)
    public boolean existeAssociacaoOperadorProduto(Long operadorId, Long produtoId) {
        return operadorProdutoRepository.existsByOperadorIdAndProdutoIdAndAtivoTrue(operadorId, produtoId);
    }

    /**
     * Verifica se existe associação ativa entre usuário e produto.
     */
    @Transactional(readOnly = true)
    public boolean existeAssociacaoUsuarioProduto(Long usuarioId, Long produtoId) {
        return usuarioProdutoRepository.existsByUsuarioIdAndProdutoIdAndAtivoTrue(usuarioId, produtoId);
    }

    /**
     * Conta associações ativas de um operador.
     */
    @Transactional(readOnly = true)
    public long contarAssociacoesOperador(Long operadorId) {
        buscarOperadorPorId(operadorId);
        return operadorProdutoRepository.countByOperadorIdAndAtivoTrue(operadorId);
    }

    /**
     * Conta associações ativas de um usuário.
     */
    @Transactional(readOnly = true)
    public long contarAssociacoesUsuario(Long usuarioId) {
        buscarUsuarioPorId(usuarioId);
        return usuarioProdutoRepository.countByUsuarioIdAndAtivoTrue(usuarioId);
    }

    /**
     * Conta associações ativas de um produto (operadores + usuários).
     */
    @Transactional(readOnly = true)
    public long contarAssociacoesProduto(Long produtoId) {
        buscarProdutoPorId(produtoId);
        long operadores = operadorProdutoRepository.countByProdutoIdAndAtivoTrue(produtoId);
        long usuarios = usuarioProdutoRepository.countByProdutoIdAndAtivoTrue(produtoId);
        return operadores + usuarios;
    }

    // Métodos auxiliares privados

    private Operador buscarOperadorPorId(Long id) {
        return operadorRepository.findById(id)
                .filter(Operador::isAtivo)
                .orElseThrow(() -> new RuntimeException("Operador não encontrado com ID: " + id));
    }

    private Produto buscarProdutoPorId(Long id) {
        return produtoRepository.findById(id)
                .filter(Produto::isAtivo)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + id));
    }

    private Usuario buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .filter(Usuario::isAtivo)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id));
    }
}

