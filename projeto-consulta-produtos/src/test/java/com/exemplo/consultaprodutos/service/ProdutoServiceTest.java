package com.exemplo.consultaprodutos.service;

import com.exemplo.consultaprodutos.dto.request.ProdutoRequestDTO;
import com.exemplo.consultaprodutos.dto.response.ProdutoResponseDTO;
import com.exemplo.consultaprodutos.entity.Produto;
import com.exemplo.consultaprodutos.mapper.ProdutoMapper;
import com.exemplo.consultaprodutos.repository.ProdutoRepository;
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
    private ProdutoMapper produtoMapper;

    @InjectMocks
    private ProdutoService produtoService;

    private Produto produto;
    private ProdutoRequestDTO produtoRequestDTO;
    private ProdutoResponseDTO produtoResponseDTO;

    @BeforeEach
    void setUp() {
        // Setup do produto
        produto = new Produto("Produto Teste", "Descrição do produto", "PRD001", 
                             new BigDecimal("150.00"), "Eletrônicos");
        produto.setId(1L);
        produto.setAtivo(true);

        // Setup do Record de request
        produtoRequestDTO = new ProdutoRequestDTO("Produto Teste", "Descrição do produto", 
                                                 "PRD001", new BigDecimal("150.00"), "Eletrônicos");

        // Setup do Record de response
        produtoResponseDTO = new ProdutoResponseDTO(1L, "Produto Teste", "Descrição do produto",
                                                   "PRD001", new BigDecimal("150.00"), "Eletrônicos", 0L);
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
        assertEquals(produtoResponseDTO.id(), resultado.id());
        assertEquals(produtoResponseDTO.nome(), resultado.nome());
        assertEquals(produtoResponseDTO.codigo(), resultado.codigo());

        verify(produtoRepository).existsByCodigoAndAtivoTrue(produtoRequestDTO.codigo());
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
        
        assertEquals("Já existe um produto com o código: " + produtoRequestDTO.codigo(), 
                     exception.getMessage());

        verify(produtoRepository).existsByCodigoAndAtivoTrue(produtoRequestDTO.codigo());
        verify(produtoMapper, never()).toEntity(any());
        verify(produtoRepository, never()).save(any());
    }

    @Test
    void buscarPorId_DeveRetornarProdutoResponseDTO_QuandoProdutoExiste() {
        // Given
        Long produtoId = 1L;
        Long totalOperadores = 3L;
        
        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produto));
        when(produtoRepository.countOperadoresByProdutoId(produtoId)).thenReturn(totalOperadores);
        when(produtoMapper.toResponseDTO(produto, totalOperadores)).thenReturn(produtoResponseDTO);

        // When
        ProdutoResponseDTO resultado = produtoService.buscarPorId(produtoId);

        // Then
        assertNotNull(resultado);
        assertEquals(produtoResponseDTO.id(), resultado.id());

        verify(produtoRepository).findById(produtoId);
        verify(produtoRepository).countOperadoresByProdutoId(produtoId);
        verify(produtoMapper).toResponseDTO(produto, totalOperadores);
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
        assertEquals("Produto Teste", resultado.get(0).nome());

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
        assertEquals("Produto Teste", resultado.get(0).nome());

        verify(produtoRepository).findByPrecoRange(precoMin, precoMax);
        verify(produtoMapper).toResponseDTOList(produtosDaFaixa);
    }

    @Test
    void buscarPorFaixaPreco_DeveLancarExcecao_QuandoPrecoMinMaiorQueMax() {
        // Given
        BigDecimal precoMin = new BigDecimal("200.00");
        BigDecimal precoMax = new BigDecimal("100.00");

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> produtoService.buscarPorFaixaPreco(precoMin, precoMax));
        
        assertEquals("Preço mínimo não pode ser maior que o preço máximo", exception.getMessage());

        verify(produtoRepository, never()).findByPrecoRange(any(), any());
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

    @Test
    void listarCategorias_DeveRetornarListaCategorias() {
        // Given
        List<String> categorias = Arrays.asList("Eletrônicos", "Móveis", "Roupas");
        when(produtoRepository.findDistinctCategorias()).thenReturn(categorias);

        // When
        List<String> resultado = produtoService.listarCategorias();

        // Then
        assertNotNull(resultado);
        assertEquals(3, resultado.size());
        assertTrue(resultado.contains("Eletrônicos"));
        assertTrue(resultado.contains("Móveis"));
        assertTrue(resultado.contains("Roupas"));

        verify(produtoRepository).findDistinctCategorias();
    }
}

