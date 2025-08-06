package com.exemplo.consultaprodutos.service;

import com.exemplo.consultaprodutos.dto.request.ProdutoRequestDTO;
import com.exemplo.consultaprodutos.dto.response.ProdutoResponseDTO;
import com.exemplo.consultaprodutos.dto.response.UsuarioResponseDTO;
import com.exemplo.consultaprodutos.entity.Produto;
import com.exemplo.consultaprodutos.entity.Usuario;
import com.exemplo.consultaprodutos.mapper.ProdutoMapper;
import com.exemplo.consultaprodutos.mapper.UsuarioMapper;
import com.exemplo.consultaprodutos.repository.ProdutoRepository;
import com.exemplo.consultaprodutos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service para operações de negócio relacionadas a Produto.
 */
@Service
@Transactional
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoMapper produtoMapper;
    private final UsuarioMapper usuarioMapper;

    @Autowired
    public ProdutoService(ProdutoRepository produtoRepository,
                         UsuarioRepository usuarioRepository,
                         ProdutoMapper produtoMapper,
                         UsuarioMapper usuarioMapper) {
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
        this.produtoMapper = produtoMapper;
        this.usuarioMapper = usuarioMapper;
    }

    /**
     * Cria um novo produto.
     */
    public ProdutoResponseDTO criarProduto(ProdutoRequestDTO requestDTO) {
        validarCodigoUnico(requestDTO.getCodigo(), null);
        
        Produto produto = produtoMapper.toEntity(requestDTO);
        produto = produtoRepository.save(produto);
        
        return produtoMapper.toResponseDTO(produto);
    }

    /**
     * Busca produto por ID.
     */
    @Transactional(readOnly = true)
    public ProdutoResponseDTO buscarPorId(Long id) {
        Produto produto = buscarProdutoPorId(id);
        Long totalOperadores = produtoRepository.countOperadoresByProdutoId(id);
        Long totalUsuarios = produtoRepository.countUsuariosByProdutoId(id);
        
        return produtoMapper.toResponseDTO(produto, totalOperadores, totalUsuarios);
    }

    /**
     * Lista todos os produtos ativos.
     */
    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> listarTodos() {
        List<Produto> produtos = produtoRepository.findByAtivoTrueOrderByNome();
        return produtoMapper.toResponseDTOList(produtos);
    }

    /**
     * Lista produtos com paginação.
     */
    @Transactional(readOnly = true)
    public Page<ProdutoResponseDTO> listarComPaginacao(Pageable pageable) {
        Page<Produto> produtos = produtoRepository.findByAtivoTrueOrderByNome(pageable);
        return produtos.map(produtoMapper::toResponseDTO);
    }

    /**
     * Busca produtos por nome.
     */
    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> buscarPorNome(String nome) {
        List<Produto> produtos = produtoRepository.findByNomeContainingIgnoreCaseAndAtivoTrue(nome);
        return produtoMapper.toResponseDTOList(produtos);
    }

    /**
     * Busca produtos por categoria.
     */
    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> buscarPorCategoria(String categoria) {
        List<Produto> produtos = produtoRepository.findByCategoriaAndAtivoTrueOrderByNome(categoria);
        return produtoMapper.toResponseDTOList(produtos);
    }

    /**
     * Busca produtos por faixa de preço.
     */
    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> buscarPorFaixaPreco(BigDecimal precoMin, BigDecimal precoMax) {
        List<Produto> produtos = produtoRepository.findByPrecoRange(precoMin, precoMax);
        return produtoMapper.toResponseDTOList(produtos);
    }

    /**
     * FUNCIONALIDADE PRINCIPAL: Busca usuários associados a um produto.
     */
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> buscarUsuariosPorProduto(Long produtoId) {
        // Verifica se o produto existe
        buscarProdutoPorId(produtoId);
        
        // Busca usuários associados
        List<Usuario> usuarios = usuarioRepository.findUsuariosByProdutoId(produtoId);
        return usuarioMapper.toResponseDTOResumoList(usuarios);
    }

    /**
     * FUNCIONALIDADE PRINCIPAL: Busca usuários associados a um produto com paginação.
     */
    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> buscarUsuariosPorProdutoComPaginacao(Long produtoId, Pageable pageable) {
        // Verifica se o produto existe
        buscarProdutoPorId(produtoId);
        
        // Busca usuários associados com paginação
        Page<Usuario> usuarios = usuarioRepository.findUsuariosByProdutoId(produtoId, pageable);
        return usuarios.map(usuarioMapper::toResponseDTOResumo);
    }

    /**
     * Atualiza um produto.
     */
    public ProdutoResponseDTO atualizarProduto(Long id, ProdutoRequestDTO requestDTO) {
        Produto produto = buscarProdutoPorId(id);
        validarCodigoUnico(requestDTO.getCodigo(), id);
        
        produtoMapper.updateEntity(produto, requestDTO);
        produto = produtoRepository.save(produto);
        
        return produtoMapper.toResponseDTO(produto);
    }

    /**
     * Remove produto (soft delete).
     */
    public void removerProduto(Long id) {
        Produto produto = buscarProdutoPorId(id);
        produto.desativar();
        produtoRepository.save(produto);
    }

    /**
     * Lista produtos que possuem usuários.
     */
    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> listarProdutosComUsuarios() {
        List<Produto> produtos = produtoRepository.findProdutosComUsuarios();
        return produtoMapper.toResponseDTOResumoList(produtos);
    }

    /**
     * Lista produtos que possuem operadores.
     */
    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> listarProdutosComOperadores() {
        List<Produto> produtos = produtoRepository.findProdutosComOperadores();
        return produtoMapper.toResponseDTOResumoList(produtos);
    }

    /**
     * Busca produto por código.
     */
    @Transactional(readOnly = true)
    public ProdutoResponseDTO buscarPorCodigo(String codigo) {
        Produto produto = produtoRepository.findByCodigoAndAtivoTrue(codigo)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com código: " + codigo));
        
        return produtoMapper.toResponseDTO(produto);
    }

    /**
     * Lista todas as categorias distintas.
     */
    @Transactional(readOnly = true)
    public List<String> listarCategorias() {
        return produtoRepository.findDistinctCategorias();
    }

    // Métodos auxiliares privados

    private Produto buscarProdutoPorId(Long id) {
        return produtoRepository.findById(id)
                .filter(Produto::isAtivo)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + id));
    }

    private void validarCodigoUnico(String codigo, Long id) {
        boolean codigoExiste = (id == null) 
            ? produtoRepository.existsByCodigoAndAtivoTrue(codigo)
            : produtoRepository.existsByCodigoAndIdNotAndAtivoTrue(codigo, id);
            
        if (codigoExiste) {
            throw new RuntimeException("Já existe um produto com o código: " + codigo);
        }
    }
}

