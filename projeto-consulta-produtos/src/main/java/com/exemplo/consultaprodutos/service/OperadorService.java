package com.exemplo.consultaprodutos.service;

import com.exemplo.consultaprodutos.dto.request.OperadorRequestDTO;
import com.exemplo.consultaprodutos.dto.response.OperadorResponseDTO;
import com.exemplo.consultaprodutos.dto.response.ProdutoResponseDTO;
import com.exemplo.consultaprodutos.entity.Operador;
import com.exemplo.consultaprodutos.entity.Produto;
import com.exemplo.consultaprodutos.mapper.OperadorMapper;
import com.exemplo.consultaprodutos.mapper.ProdutoMapper;
import com.exemplo.consultaprodutos.repository.OperadorRepository;
import com.exemplo.consultaprodutos.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service para operações de negócio relacionadas a Operador.
 */
@Service
@Transactional
public class OperadorService {

    private final OperadorRepository operadorRepository;
    private final ProdutoRepository produtoRepository;
    private final OperadorMapper operadorMapper;
    private final ProdutoMapper produtoMapper;

    @Autowired
    public OperadorService(OperadorRepository operadorRepository,
                          ProdutoRepository produtoRepository,
                          OperadorMapper operadorMapper,
                          ProdutoMapper produtoMapper) {
        this.operadorRepository = operadorRepository;
        this.produtoRepository = produtoRepository;
        this.operadorMapper = operadorMapper;
        this.produtoMapper = produtoMapper;
    }

    /**
     * Cria um novo operador.
     */
    public OperadorResponseDTO criarOperador(OperadorRequestDTO requestDTO) {
        validarEmailUnico(requestDTO.getEmail(), null);
        
        Operador operador = operadorMapper.toEntity(requestDTO);
        operador = operadorRepository.save(operador);
        
        return operadorMapper.toResponseDTO(operador);
    }

    /**
     * Busca operador por ID.
     */
    @Transactional(readOnly = true)
    public OperadorResponseDTO buscarPorId(Long id) {
        Operador operador = buscarOperadorPorId(id);
        Long totalProdutos = operadorRepository.countProdutosByOperadorId(id);
        
        return operadorMapper.toResponseDTO(operador, totalProdutos);
    }

    /**
     * Lista todos os operadores ativos.
     */
    @Transactional(readOnly = true)
    public List<OperadorResponseDTO> listarTodos() {
        List<Operador> operadores = operadorRepository.findByAtivoTrueOrderByNome();
        return operadorMapper.toResponseDTOList(operadores);
    }

    /**
     * Lista operadores com paginação.
     */
    @Transactional(readOnly = true)
    public Page<OperadorResponseDTO> listarComPaginacao(Pageable pageable) {
        Page<Operador> operadores = operadorRepository.findByAtivoTrueOrderByNome(pageable);
        return operadores.map(operadorMapper::toResponseDTO);
    }

    /**
     * Busca operadores por nome.
     */
    @Transactional(readOnly = true)
    public List<OperadorResponseDTO> buscarPorNome(String nome) {
        List<Operador> operadores = operadorRepository.findByNomeContainingIgnoreCaseAndAtivoTrue(nome);
        return operadorMapper.toResponseDTOList(operadores);
    }

    /**
     * FUNCIONALIDADE PRINCIPAL: Busca produtos associados a um operador.
     */
    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> buscarProdutosPorOperador(Long operadorId) {
        // Verifica se o operador existe
        buscarOperadorPorId(operadorId);
        
        // Busca produtos associados
        List<Produto> produtos = produtoRepository.findProdutosByOperadorId(operadorId);
        return produtoMapper.toResponseDTOResumoList(produtos);
    }

    /**
     * FUNCIONALIDADE PRINCIPAL: Busca produtos associados a um operador com paginação.
     */
    @Transactional(readOnly = true)
    public Page<ProdutoResponseDTO> buscarProdutosPorOperadorComPaginacao(Long operadorId, Pageable pageable) {
        // Verifica se o operador existe
        buscarOperadorPorId(operadorId);
        
        // Busca produtos associados com paginação
        Page<Produto> produtos = produtoRepository.findProdutosByOperadorId(operadorId, pageable);
        return produtos.map(produtoMapper::toResponseDTOResumo);
    }

    /**
     * Busca produtos associados a um operador por categoria.
     */
    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> buscarProdutosPorOperadorECategoria(Long operadorId, String categoria) {
        // Verifica se o operador existe
        buscarOperadorPorId(operadorId);
        
        // Busca produtos associados por categoria
        List<Produto> produtos = produtoRepository.findProdutosByOperadorIdAndCategoria(operadorId, categoria);
        return produtoMapper.toResponseDTOResumoList(produtos);
    }

    /**
     * Atualiza um operador.
     */
    public OperadorResponseDTO atualizarOperador(Long id, OperadorRequestDTO requestDTO) {
        Operador operador = buscarOperadorPorId(id);
        validarEmailUnico(requestDTO.getEmail(), id);
        
        operadorMapper.updateEntity(operador, requestDTO);
        operador = operadorRepository.save(operador);
        
        return operadorMapper.toResponseDTO(operador);
    }

    /**
     * Remove operador (soft delete).
     */
    public void removerOperador(Long id) {
        Operador operador = buscarOperadorPorId(id);
        operador.desativar();
        operadorRepository.save(operador);
    }

    /**
     * Lista operadores que possuem produtos.
     */
    @Transactional(readOnly = true)
    public List<OperadorResponseDTO> listarOperadoresComProdutos() {
        List<Operador> operadores = operadorRepository.findOperadoresComProdutos();
        return operadorMapper.toResponseDTOResumoList(operadores);
    }

    /**
     * Busca operador por email.
     */
    @Transactional(readOnly = true)
    public OperadorResponseDTO buscarPorEmail(String email) {
        Operador operador = operadorRepository.findByEmailAndAtivoTrue(email)
                .orElseThrow(() -> new RuntimeException("Operador não encontrado com email: " + email));
        
        return operadorMapper.toResponseDTO(operador);
    }

    // Métodos auxiliares privados

    private Operador buscarOperadorPorId(Long id) {
        return operadorRepository.findById(id)
                .filter(Operador::isAtivo)
                .orElseThrow(() -> new RuntimeException("Operador não encontrado com ID: " + id));
    }

    private void validarEmailUnico(String email, Long id) {
        boolean emailExiste = (id == null) 
            ? operadorRepository.existsByEmailAndAtivoTrue(email)
            : operadorRepository.existsByEmailAndIdNotAndAtivoTrue(email, id);
            
        if (emailExiste) {
            throw new RuntimeException("Já existe um operador com o email: " + email);
        }
    }
}

