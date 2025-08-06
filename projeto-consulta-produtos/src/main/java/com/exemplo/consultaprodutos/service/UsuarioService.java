package com.exemplo.consultaprodutos.service;

import com.exemplo.consultaprodutos.dto.request.UsuarioRequestDTO;
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

import java.util.List;

/**
 * Service para operações de negócio relacionadas a Usuario.
 */
@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioMapper usuarioMapper;
    private final ProdutoMapper produtoMapper;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository,
                         ProdutoRepository produtoRepository,
                         UsuarioMapper usuarioMapper,
                         ProdutoMapper produtoMapper) {
        this.usuarioRepository = usuarioRepository;
        this.produtoRepository = produtoRepository;
        this.usuarioMapper = usuarioMapper;
        this.produtoMapper = produtoMapper;
    }

    /**
     * Cria um novo usuário.
     */
    public UsuarioResponseDTO criarUsuario(UsuarioRequestDTO requestDTO) {
        validarEmailUnico(requestDTO.getEmail(), null);
        
        Usuario usuario = usuarioMapper.toEntity(requestDTO);
        usuario = usuarioRepository.save(usuario);
        
        return usuarioMapper.toResponseDTO(usuario);
    }

    /**
     * Busca usuário por ID.
     */
    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = buscarUsuarioPorId(id);
        Long totalProdutos = usuarioRepository.countProdutosByUsuarioId(id);
        
        return usuarioMapper.toResponseDTO(usuario, totalProdutos);
    }

    /**
     * Lista todos os usuários ativos.
     */
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        List<Usuario> usuarios = usuarioRepository.findByAtivoTrueOrderByNome();
        return usuarioMapper.toResponseDTOList(usuarios);
    }

    /**
     * Lista usuários com paginação.
     */
    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> listarComPaginacao(Pageable pageable) {
        Page<Usuario> usuarios = usuarioRepository.findByAtivoTrueOrderByNome(pageable);
        return usuarios.map(usuarioMapper::toResponseDTO);
    }

    /**
     * Busca usuários por nome.
     */
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> buscarPorNome(String nome) {
        List<Usuario> usuarios = usuarioRepository.findByNomeContainingIgnoreCaseAndAtivoTrue(nome);
        return usuarioMapper.toResponseDTOList(usuarios);
    }

    /**
     * Busca produtos associados a um usuário.
     */
    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> buscarProdutosPorUsuario(Long usuarioId) {
        // Verifica se o usuário existe
        buscarUsuarioPorId(usuarioId);
        
        // Busca produtos através da entidade de relacionamento
        List<Produto> produtos = produtoRepository.findAll().stream()
                .filter(produto -> produto.getUsuarioProdutos().stream()
                        .anyMatch(up -> up.getUsuario().getId().equals(usuarioId) && up.isAtivo()))
                .toList();
        
        return produtoMapper.toResponseDTOResumoList(produtos);
    }

    /**
     * Busca usuários que possuem produtos de uma categoria específica.
     */
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> buscarUsuariosPorProdutoCategoria(String categoria) {
        List<Usuario> usuarios = usuarioRepository.findUsuariosByProdutoCategoria(categoria);
        return usuarioMapper.toResponseDTOResumoList(usuarios);
    }

    /**
     * Busca usuários que possuem produtos de um operador específico.
     */
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> buscarUsuariosPorOperador(Long operadorId) {
        List<Usuario> usuarios = usuarioRepository.findUsuariosByOperadorId(operadorId);
        return usuarioMapper.toResponseDTOResumoList(usuarios);
    }

    /**
     * Atualiza um usuário.
     */
    public UsuarioResponseDTO atualizarUsuario(Long id, UsuarioRequestDTO requestDTO) {
        Usuario usuario = buscarUsuarioPorId(id);
        validarEmailUnico(requestDTO.getEmail(), id);
        
        usuarioMapper.updateEntity(usuario, requestDTO);
        usuario = usuarioRepository.save(usuario);
        
        return usuarioMapper.toResponseDTO(usuario);
    }

    /**
     * Remove usuário (soft delete).
     */
    public void removerUsuario(Long id) {
        Usuario usuario = buscarUsuarioPorId(id);
        usuario.desativar();
        usuarioRepository.save(usuario);
    }

    /**
     * Lista usuários que possuem produtos.
     */
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarUsuariosComProdutos() {
        List<Usuario> usuarios = usuarioRepository.findUsuariosComProdutos();
        return usuarioMapper.toResponseDTOResumoList(usuarios);
    }

    /**
     * Lista usuários que possuem produtos com paginação.
     */
    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> listarUsuariosComProdutosComPaginacao(Pageable pageable) {
        Page<Usuario> usuarios = usuarioRepository.findUsuariosComProdutos(pageable);
        return usuarios.map(usuarioMapper::toResponseDTOResumo);
    }

    /**
     * Busca usuário por email.
     */
    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmailAndAtivoTrue(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com email: " + email));
        
        return usuarioMapper.toResponseDTO(usuario);
    }

    /**
     * Conta quantos produtos um usuário possui.
     */
    @Transactional(readOnly = true)
    public Long contarProdutosPorUsuario(Long usuarioId) {
        // Verifica se o usuário existe
        buscarUsuarioPorId(usuarioId);
        
        return usuarioRepository.countProdutosByUsuarioId(usuarioId);
    }

    // Métodos auxiliares privados

    private Usuario buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .filter(Usuario::isAtivo)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id));
    }

    private void validarEmailUnico(String email, Long id) {
        boolean emailExiste = (id == null) 
            ? usuarioRepository.existsByEmailAndAtivoTrue(email)
            : usuarioRepository.existsByEmailAndIdNotAndAtivoTrue(email, id);
            
        if (emailExiste) {
            throw new RuntimeException("Já existe um usuário com o email: " + email);
        }
    }
}

