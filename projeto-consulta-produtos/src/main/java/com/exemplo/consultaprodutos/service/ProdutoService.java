package com.exemplo.consultaprodutos.service;

import com.exemplo.consultaprodutos.dto.request.ProdutoRequestDTO;
import com.exemplo.consultaprodutos.dto.response.ProdutoResponseDTO;
import com.exemplo.consultaprodutos.entity.Produto;
import com.exemplo.consultaprodutos.mapper.ProdutoMapper;
import com.exemplo.consultaprodutos.repository.ProdutoRepository;
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
    private final ProdutoMapper produtoMapper;

    @Autowired
    public ProdutoService(ProdutoRepository produtoRepository, ProdutoMapper produtoMapper) {
        this.produtoRepository = produtoRepository;
        this.produtoMapper = produtoMapper;
    }

    /**
     * Cria um novo produto.
     */
    public ProdutoResponseDTO criarProduto(ProdutoRequestDTO requestDTO) {
        // Validar se já existe produto com o mesmo código
        if (produtoRepository.existsByCodigoAndAtivoTrue(requestDTO.getCodigo())) {
            throw new RuntimeException("Já existe um produto com o código: " + requestDTO.getCodigo());
        }

        Produto produto = produtoMapper.toEntity(requestDTO);
        produto = produtoRepository.save(produto);
        
        return produtoMapper.toResponseDTO(produto);
    }

    /**
     * Busca produto por ID.
     */
    @Transactional(readOnly = true)
    public ProdutoResponseDTO buscarPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + id));

        // Buscar contadores
        long totalOperadores = produtoRepository.countOperadoresByProdutoId(id);
        
        return produtoMapper.toResponseDTO(produto, totalOperadores);
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
     * Busca produtos por nome (busca parcial).
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
        if (precoMin.compareTo(precoMax) > 0) {
            throw new RuntimeException("Preço mínimo não pode ser maior que o preço máximo");
        }
        
        List<Produto> produtos = produtoRepository.findByPrecoRange(precoMin, precoMax);
        return produtoMapper.toResponseDTOList(produtos);
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
     * Lista produtos que possuem operadores.
     */
    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> listarProdutosComOperadores() {
        List<Produto> produtos = produtoRepository.findProdutosComOperadores();
        return produtoMapper.toResponseDTOList(produtos);
    }

    /**
     * Lista todas as categorias distintas.
     */
    @Transactional(readOnly = true)
    public List<String> listarCategorias() {
        return produtoRepository.findDistinctCategorias();
    }

    /**
     * Atualiza um produto.
     */
    public ProdutoResponseDTO atualizarProduto(Long id, ProdutoRequestDTO requestDTO) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + id));

        // Validar se o código não está sendo usado por outro produto
        if (!produto.getCodigo().equals(requestDTO.getCodigo()) && 
            produtoRepository.existsByCodigoAndIdNotAndAtivoTrue(requestDTO.getCodigo(), id)) {
            throw new RuntimeException("Já existe outro produto com o código: " + requestDTO.getCodigo());
        }

        // Atualizar campos
        produto.setNome(requestDTO.getNome());
        produto.setDescricao(requestDTO.getDescricao());
        produto.setCodigo(requestDTO.getCodigo());
        produto.setPreco(requestDTO.getPreco());
        produto.setCategoria(requestDTO.getCategoria());

        produto = produtoRepository.save(produto);
        return produtoMapper.toResponseDTO(produto);
    }

    /**
     * Busca produtos associados a um operador (FUNCIONALIDADE PRINCIPAL).
     */
    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> findProdutosByOperadorId(Long operadorId) {
        List<Produto> produtos = produtoRepository.findProdutosByOperadorId(operadorId);
        return produtoMapper.toResponseDTOList(produtos);
    }

    /**
     * Busca produtos associados a um operador com paginação.
     */
    @Transactional(readOnly = true)
    public Page<ProdutoResponseDTO> findProdutosByOperadorId(Long operadorId, Pageable pageable) {
        Page<Produto> produtos = produtoRepository.findProdutosByOperadorId(operadorId, pageable);
        return produtos.map(produtoMapper::toResponseDTO);
    }

    /**
     * Busca produtos associados a um operador por categoria.
     */
    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> findProdutosByOperadorIdAndCategoria(Long operadorId, String categoria) {
        List<Produto> produtos = produtoRepository.findProdutosByOperadorIdAndCategoria(operadorId, categoria);
        return produtoMapper.toResponseDTOList(produtos);
    }

    /**
     * Remove produto (soft delete).
     */
    public void removerProduto(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + id));

        produto.setAtivo(false);
        produtoRepository.save(produto);
    }
}

