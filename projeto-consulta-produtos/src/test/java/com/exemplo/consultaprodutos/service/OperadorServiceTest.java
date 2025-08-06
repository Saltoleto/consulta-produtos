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
 * Testes unitários para OperadorService.
 */
@ExtendWith(MockitoExtension.class)
class OperadorServiceTest {

    @Mock
    private OperadorRepository operadorRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private OperadorMapper operadorMapper;

    @Mock
    private ProdutoMapper produtoMapper;

    @InjectMocks
    private OperadorService operadorService;

    private Operador operador;
    private OperadorRequestDTO operadorRequestDTO;
    private OperadorResponseDTO operadorResponseDTO;
    private Produto produto1;
    private Produto produto2;
    private List<Produto> produtos;

    @BeforeEach
    void setUp() {
        // Setup do operador
        operador = new Operador("João Silva", "joao@email.com", "11999999999");
        operador.setId(1L);
        operador.setAtivo(true);

        // Setup do DTO de request
        operadorRequestDTO = new OperadorRequestDTO("João Silva", "joao@email.com", "11999999999");

        // Setup do DTO de response
        operadorResponseDTO = new OperadorResponseDTO(1L, "João Silva", "joao@email.com");

        // Setup dos produtos
        produto1 = new Produto("Produto A", "PRD001", new BigDecimal("100.00"));
        produto1.setId(1L);
        produto1.setAtivo(true);

        produto2 = new Produto("Produto B", "PRD002", new BigDecimal("200.00"));
        produto2.setId(2L);
        produto2.setAtivo(true);

        produtos = Arrays.asList(produto1, produto2);
    }

    @Test
    void criarOperador_DeveRetornarOperadorResponseDTO_QuandoDadosValidos() {
        // Given
        when(operadorRepository.existsByEmailAndAtivoTrue(anyString())).thenReturn(false);
        when(operadorMapper.toEntity(operadorRequestDTO)).thenReturn(operador);
        when(operadorRepository.save(operador)).thenReturn(operador);
        when(operadorMapper.toResponseDTO(operador)).thenReturn(operadorResponseDTO);

        // When
        OperadorResponseDTO resultado = operadorService.criarOperador(operadorRequestDTO);

        // Then
        assertNotNull(resultado);
        assertEquals(operadorResponseDTO.getId(), resultado.getId());
        assertEquals(operadorResponseDTO.getNome(), resultado.getNome());
        assertEquals(operadorResponseDTO.getEmail(), resultado.getEmail());

        verify(operadorRepository).existsByEmailAndAtivoTrue(operadorRequestDTO.getEmail());
        verify(operadorMapper).toEntity(operadorRequestDTO);
        verify(operadorRepository).save(operador);
        verify(operadorMapper).toResponseDTO(operador);
    }

    @Test
    void criarOperador_DeveLancarExcecao_QuandoEmailJaExiste() {
        // Given
        when(operadorRepository.existsByEmailAndAtivoTrue(anyString())).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> operadorService.criarOperador(operadorRequestDTO));
        
        assertEquals("Já existe um operador com o email: " + operadorRequestDTO.getEmail(), 
                     exception.getMessage());

        verify(operadorRepository).existsByEmailAndAtivoTrue(operadorRequestDTO.getEmail());
        verify(operadorMapper, never()).toEntity(any());
        verify(operadorRepository, never()).save(any());
    }

    @Test
    void buscarPorId_DeveRetornarOperadorResponseDTO_QuandoOperadorExiste() {
        // Given
        Long operadorId = 1L;
        Long totalProdutos = 2L;
        
        when(operadorRepository.findById(operadorId)).thenReturn(Optional.of(operador));
        when(operadorRepository.countProdutosByOperadorId(operadorId)).thenReturn(totalProdutos);
        when(operadorMapper.toResponseDTO(operador, totalProdutos)).thenReturn(operadorResponseDTO);

        // When
        OperadorResponseDTO resultado = operadorService.buscarPorId(operadorId);

        // Then
        assertNotNull(resultado);
        assertEquals(operadorResponseDTO.getId(), resultado.getId());

        verify(operadorRepository).findById(operadorId);
        verify(operadorRepository).countProdutosByOperadorId(operadorId);
        verify(operadorMapper).toResponseDTO(operador, totalProdutos);
    }

    @Test
    void buscarPorId_DeveLancarExcecao_QuandoOperadorNaoExiste() {
        // Given
        Long operadorId = 999L;
        when(operadorRepository.findById(operadorId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> operadorService.buscarPorId(operadorId));
        
        assertEquals("Operador não encontrado com ID: " + operadorId, exception.getMessage());

        verify(operadorRepository).findById(operadorId);
        verify(operadorRepository, never()).countProdutosByOperadorId(any());
        verify(operadorMapper, never()).toResponseDTO(any(), any());
    }

    @Test
    void buscarProdutosPorOperador_DeveRetornarListaProdutos_QuandoOperadorExiste() {
        // Given
        Long operadorId = 1L;
        List<ProdutoResponseDTO> produtosResponseDTO = Arrays.asList(
            new ProdutoResponseDTO(1L, "Produto A", "PRD001", new BigDecimal("100.00")),
            new ProdutoResponseDTO(2L, "Produto B", "PRD002", new BigDecimal("200.00"))
        );

        when(operadorRepository.findById(operadorId)).thenReturn(Optional.of(operador));
        when(produtoRepository.findProdutosByOperadorId(operadorId)).thenReturn(produtos);
        when(produtoMapper.toResponseDTOResumoList(produtos)).thenReturn(produtosResponseDTO);

        // When
        List<ProdutoResponseDTO> resultado = operadorService.buscarProdutosPorOperador(operadorId);

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Produto A", resultado.get(0).getNome());
        assertEquals("Produto B", resultado.get(1).getNome());

        verify(operadorRepository).findById(operadorId);
        verify(produtoRepository).findProdutosByOperadorId(operadorId);
        verify(produtoMapper).toResponseDTOResumoList(produtos);
    }

    @Test
    void buscarProdutosPorOperador_DeveLancarExcecao_QuandoOperadorNaoExiste() {
        // Given
        Long operadorId = 999L;
        when(operadorRepository.findById(operadorId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> operadorService.buscarProdutosPorOperador(operadorId));
        
        assertEquals("Operador não encontrado com ID: " + operadorId, exception.getMessage());

        verify(operadorRepository).findById(operadorId);
        verify(produtoRepository, never()).findProdutosByOperadorId(any());
        verify(produtoMapper, never()).toResponseDTOResumoList(any());
    }

    @Test
    void removerOperador_DeveDesativarOperador_QuandoOperadorExiste() {
        // Given
        Long operadorId = 1L;
        when(operadorRepository.findById(operadorId)).thenReturn(Optional.of(operador));
        when(operadorRepository.save(operador)).thenReturn(operador);

        // When
        operadorService.removerOperador(operadorId);

        // Then
        assertFalse(operador.isAtivo());
        verify(operadorRepository).findById(operadorId);
        verify(operadorRepository).save(operador);
    }
}

