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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para ProdutoService.
 */
@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ProdutoMapper produtoMapper;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private ProdutoService produtoService;

    private Produto produto;
    private ProdutoRequestDTO produtoRequestDTO;
    private ProdutoResponseDTO produtoResponseDTO;
    private Usuario usuario1;
    private Usuario usuario2;
    private List<Usuario> usuarios;

    @BeforeEach
    void setUp() {
        // Setup do produto
        produto = new Produto("Produto Teste", "PRD001", new BigDecimal("150.00"));
        produto.setId(1L);
        produto.setAtivo(true);
        produto.setCategoria("Eletrônicos");

        // Setup do DTO de request
        produtoRequestDTO = new ProdutoRequestDTO("Produto Teste", "Descrição do produto", 
                                                 "PRD001", new BigDecimal("150.00"), "Eletrônicos");

        // Setup do DTO de response
        produtoResponseDTO = new ProdutoResponseDTO(1L, "Produto Teste", "PRD001", new BigDecimal("150.00"));

        // Setup dos usuários
        usuario1 = new Usuario("Maria Silva", "maria@email.com", "11888888888");
        usuario1.setId(1L);
        usuario1.setAtivo(true);

        usuario2 = new Usuario("Pedro Santos", "pedro@email.com", "11777777777");
        usuario2.setId(2L);
        usuario2.setAtivo(true);

        usuarios = Arrays.asList(usuario1, usuario2);
    }

    @Test
    void criarProduto_DeveRetornarProdutoResponseDTO_QuandoDadosValidos() {
        // Given
        when(produtoRepository.existsByCodigoAndAtivoTrue(anyString())).thenReturn(false);
        when(produtoMapper.toEntity(produtoRequestDTO)).thenReturn(produto);
        when(produtoRepository.save(produto)).thenReturn(produto);
        when(produtoMapper.toResponseDTO(produto)).thenReturn(produtoResponseDTO);

        // When
        ProdutoResponseDTO resultado = produtoService.criarProduto(produtoRequestDTO);

        // Then
        assertNotNull(resultado);
        assertEquals(produtoResponseDTO.getId(), resultado.getId());
        assertEquals(produtoResponseDTO.getNome(), resultado.getNome());
        assertEquals(produtoResponseDTO.getCodigo(), resultado.getCodigo());

        verify(produtoRepository).existsByCodigoAndAtivoTrue(produtoRequestDTO.getCodigo());
        verify(produtoMapper).toEntity(produtoRequestDTO);
        verify(produtoRepository).save(produto);
        verify(produtoMapper).toResponseDTO(produto);
    }

    @Test
    void criarProduto_DeveLancarExcecao_QuandoCodigoJaExiste() {
        // Given
        when(produtoRepository.existsByCodigoAndAtivoTrue(anyString())).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> produtoService.criarProduto(produtoRequestDTO));
        
        assertEquals("Já existe um produto com o código: " + produtoRequestDTO.getCodigo(), 
                     exception.getMessage());

        verify(produtoRepository).existsByCodigoAndAtivoTrue(produtoRequestDTO.getCodigo());
        verify(produtoMapper, never()).toEntity(any());
        verify(produtoRepository, never()).save(any());
    }

    @Test
    void buscarPorId_DeveRetornarProdutoResponseDTO_QuandoProdutoExiste() {
        // Given
        Long produtoId = 1L;
        Long totalOperadores = 3L;
        Long totalUsuarios = 5L;
        
        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produto));
        when(produtoRepository.countOperadoresByProdutoId(produtoId)).thenReturn(totalOperadores);
        when(produtoRepository.countUsuariosByProdutoId(produtoId)).thenReturn(totalUsuarios);
        when(produtoMapper.toResponseDTO(produto, totalOperadores, totalUsuarios)).thenReturn(produtoResponseDTO);

        // When
        ProdutoResponseDTO resultado = produtoService.buscarPorId(produtoId);

        // Then
        assertNotNull(resultado);
        assertEquals(produtoResponseDTO.getId(), resultado.getId());

        verify(produtoRepository).findById(produtoId);
        verify(produtoRepository).countOperadoresByProdutoId(produtoId);
        verify(produtoRepository).countUsuariosByProdutoId(produtoId);
        verify(produtoMapper).toResponseDTO(produto, totalOperadores, totalUsuarios);
    }

    @Test
    void buscarUsuariosPorProduto_DeveRetornarListaUsuarios_QuandoProdutoExiste() {
        // Given
        Long produtoId = 1L;
        List<UsuarioResponseDTO> usuariosResponseDTO = Arrays.asList(
            new UsuarioResponseDTO(1L, "Maria Silva", "maria@email.com"),
            new UsuarioResponseDTO(2L, "Pedro Santos", "pedro@email.com")
        );

        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produto));
        when(usuarioRepository.findUsuariosByProdutoId(produtoId)).thenReturn(usuarios);
        when(usuarioMapper.toResponseDTOResumoList(usuarios)).thenReturn(usuariosResponseDTO);

        // When
        List<UsuarioResponseDTO> resultado = produtoService.buscarUsuariosPorProduto(produtoId);

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Maria Silva", resultado.get(0).getNome());
        assertEquals("Pedro Santos", resultado.get(1).getNome());

        verify(produtoRepository).findById(produtoId);
        verify(usuarioRepository).findUsuariosByProdutoId(produtoId);
        verify(usuarioMapper).toResponseDTOResumoList(usuarios);
    }

    @Test
    void buscarUsuariosPorProduto_DeveLancarExcecao_QuandoProdutoNaoExiste() {
        // Given
        Long produtoId = 999L;
        when(produtoRepository.findById(produtoId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> produtoService.buscarUsuariosPorProduto(produtoId));
        
        assertEquals("Produto não encontrado com ID: " + produtoId, exception.getMessage());

        verify(produtoRepository).findById(produtoId);
        verify(usuarioRepository, never()).findUsuariosByProdutoId(any());
        verify(usuarioMapper, never()).toResponseDTOResumoList(any());
    }

    @Test
    void buscarPorCategoria_DeveRetornarListaProdutos_QuandoCategoriaExiste() {
        // Given
        String categoria = "Eletrônicos";
        List<Produto> produtosDaCategoria = Arrays.asList(produto);
        List<ProdutoResponseDTO> produtosResponseDTO = Arrays.asList(produtoResponseDTO);

        when(produtoRepository.findByCategoriaAndAtivoTrueOrderByNome(categoria)).thenReturn(produtosDaCategoria);
        when(produtoMapper.toResponseDTOList(produtosDaCategoria)).thenReturn(produtosResponseDTO);

        // When
        List<ProdutoResponseDTO> resultado = produtoService.buscarPorCategoria(categoria);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Produto Teste", resultado.get(0).getNome());

        verify(produtoRepository).findByCategoriaAndAtivoTrueOrderByNome(categoria);
        verify(produtoMapper).toResponseDTOList(produtosDaCategoria);
    }

    @Test
    void buscarPorFaixaPreco_DeveRetornarListaProdutos_QuandoFaixaValida() {
        // Given
        BigDecimal precoMin = new BigDecimal("100.00");
        BigDecimal precoMax = new BigDecimal("200.00");
        List<Produto> produtosDaFaixa = Arrays.asList(produto);
        List<ProdutoResponseDTO> produtosResponseDTO = Arrays.asList(produtoResponseDTO);

        when(produtoRepository.findByPrecoRange(precoMin, precoMax)).thenReturn(produtosDaFaixa);
        when(produtoMapper.toResponseDTOList(produtosDaFaixa)).thenReturn(produtosResponseDTO);

        // When
        List<ProdutoResponseDTO> resultado = produtoService.buscarPorFaixaPreco(precoMin, precoMax);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Produto Teste", resultado.get(0).getNome());

        verify(produtoRepository).findByPrecoRange(precoMin, precoMax);
        verify(produtoMapper).toResponseDTOList(produtosDaFaixa);
    }

    @Test
    void removerProduto_DeveDesativarProduto_QuandoProdutoExiste() {
        // Given
        Long produtoId = 1L;
        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(produto)).thenReturn(produto);

        // When
        produtoService.removerProduto(produtoId);

        // Then
        assertFalse(produto.isAtivo());
        verify(produtoRepository).findById(produtoId);
        verify(produtoRepository).save(produto);
    }
}

