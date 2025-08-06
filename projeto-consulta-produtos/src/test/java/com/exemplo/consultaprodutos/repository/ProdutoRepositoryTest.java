package com.exemplo.consultaprodutos.repository;

import com.exemplo.consultaprodutos.entity.Operador;
import com.exemplo.consultaprodutos.entity.OperadorProduto;
import com.exemplo.consultaprodutos.entity.Produto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integração para ProdutoRepository.
 */
@DataJpaTest
@ActiveProfiles("test")
class ProdutoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private OperadorRepository operadorRepository;

    @Autowired
    private OperadorProdutoRepository operadorProdutoRepository;

    private Produto produto1;
    private Produto produto2;
    private Produto produto3;
    private Operador operador1;
    private Operador operador2;

    @BeforeEach
    void setUp() {
        // Criar produtos
        produto1 = new Produto("Smartphone", "Smartphone Android", "SMART001", 
                              new BigDecimal("800.00"), "Eletrônicos");
        produto2 = new Produto("Notebook", "Notebook para trabalho", "NOTE001", 
                              new BigDecimal("2500.00"), "Eletrônicos");
        produto3 = new Produto("Mesa", "Mesa de escritório", "MESA001", 
                              new BigDecimal("300.00"), "Móveis");

        // Criar operadores
        operador1 = new Operador("João Operador", "joao@operador.com", "11999999999");
        operador2 = new Operador("Maria Operadora", "maria@operador.com", "11888888888");

        // Persistir entidades
        entityManager.persistAndFlush(produto1);
        entityManager.persistAndFlush(produto2);
        entityManager.persistAndFlush(produto3);
        entityManager.persistAndFlush(operador1);
        entityManager.persistAndFlush(operador2);

        // Criar associações
        OperadorProduto op1 = new OperadorProduto(operador1, produto1);
        OperadorProduto op2 = new OperadorProduto(operador1, produto2);
        OperadorProduto op3 = new OperadorProduto(operador2, produto1);

        entityManager.persistAndFlush(op1);
        entityManager.persistAndFlush(op2);
        entityManager.persistAndFlush(op3);

        entityManager.clear();
    }

    @Test
    void findByCodigoAndAtivoTrue_DeveRetornarProduto_QuandoCodigoExiste() {
        // When
        Optional<Produto> resultado = produtoRepository.findByCodigoAndAtivoTrue("SMART001");

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("Smartphone", resultado.get().getNome());
        assertEquals("SMART001", resultado.get().getCodigo());
    }

    @Test
    void findByCodigoAndAtivoTrue_DeveRetornarEmpty_QuandoCodigoNaoExiste() {
        // When
        Optional<Produto> resultado = produtoRepository.findByCodigoAndAtivoTrue("INEXISTENTE");

        // Then
        assertFalse(resultado.isPresent());
    }

    @Test
    void findByNomeContainingIgnoreCaseAndAtivoTrue_DeveRetornarProdutos_QuandoNomeContem() {
        // When
        List<Produto> resultado = produtoRepository.findByNomeContainingIgnoreCaseAndAtivoTrue("smart");

        // Then
        assertEquals(1, resultado.size());
        assertEquals("Smartphone", resultado.get(0).getNome());
    }

    @Test
    void findByCategoriaAndAtivoTrueOrderByNome_DeveRetornarProdutosDaCategoria() {
        // When
        List<Produto> resultado = produtoRepository.findByCategoriaAndAtivoTrueOrderByNome("Eletrônicos");

        // Then
        assertEquals(2, resultado.size());
        assertEquals("Notebook", resultado.get(0).getNome()); // Ordenado por nome
        assertEquals("Smartphone", resultado.get(1).getNome());
    }

    @Test
    void findByPrecoRange_DeveRetornarProdutosNaFaixa() {
        // When
        List<Produto> resultado = produtoRepository.findByPrecoRange(
            new BigDecimal("500.00"), new BigDecimal("1000.00"));

        // Then
        assertEquals(1, resultado.size());
        assertEquals("Smartphone", resultado.get(0).getNome());
    }

    @Test
    void findProdutosByOperadorId_DeveRetornarProdutosDoOperador() {
        // When
        List<Produto> resultado = produtoRepository.findProdutosByOperadorId(operador1.getId());

        // Then
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().anyMatch(p -> p.getNome().equals("Smartphone")));
        assertTrue(resultado.stream().anyMatch(p -> p.getNome().equals("Notebook")));
    }

    @Test
    void findProdutosByOperadorIdAndCategoria_DeveRetornarProdutosPorOperadorECategoria() {
        // When
        List<Produto> resultado = produtoRepository.findProdutosByOperadorIdAndCategoria(
            operador1.getId(), "Eletrônicos");

        // Then
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(p -> p.getCategoria().equals("Eletrônicos")));
    }

    @Test
    void findProdutosComOperadores_DeveRetornarProdutosQueTemOperadores() {
        // When
        List<Produto> resultado = produtoRepository.findProdutosComOperadores();

        // Then
        assertEquals(2, resultado.size()); // produto1 e produto2 têm operadores
        assertTrue(resultado.stream().anyMatch(p -> p.getNome().equals("Smartphone")));
        assertTrue(resultado.stream().anyMatch(p -> p.getNome().equals("Notebook")));
    }

    @Test
    void countOperadoresByProdutoId_DeveRetornarQuantidadeCorreta() {
        // When
        long count = produtoRepository.countOperadoresByProdutoId(produto1.getId());

        // Then
        assertEquals(2, count); // operador1 e operador2
    }

    @Test
    void findDistinctCategorias_DeveRetornarCategoriasDistintas() {
        // When
        List<String> categorias = produtoRepository.findDistinctCategorias();

        // Then
        assertEquals(2, categorias.size());
        assertTrue(categorias.contains("Eletrônicos"));
        assertTrue(categorias.contains("Móveis"));
    }

    @Test
    void findProdutosByOperadorId_ComPaginacao_DeveRetornarPaginaCorreta() {
        // When
        Page<Produto> resultado = produtoRepository.findProdutosByOperadorId(
            operador1.getId(), PageRequest.of(0, 1));

        // Then
        assertEquals(1, resultado.getContent().size());
        assertEquals(2, resultado.getTotalElements());
        assertEquals(2, resultado.getTotalPages());
    }

    @Test
    void existsByCodigoAndAtivoTrue_DeveRetornarTrue_QuandoCodigoExiste() {
        // When
        boolean existe = produtoRepository.existsByCodigoAndAtivoTrue("SMART001");

        // Then
        assertTrue(existe);
    }

    @Test
    void existsByCodigoAndAtivoTrue_DeveRetornarFalse_QuandoCodigoNaoExiste() {
        // When
        boolean existe = produtoRepository.existsByCodigoAndAtivoTrue("INEXISTENTE");

        // Then
        assertFalse(existe);
    }

    @Test
    void existsByCodigoAndIdNotAndAtivoTrue_DeveRetornarFalse_QuandoCodigoNaoExisteEmOutroProduto() {
        // When
        boolean existe = produtoRepository.existsByCodigoAndIdNotAndAtivoTrue("SMART001", produto1.getId());

        // Then
        assertFalse(existe); // Mesmo código, mas é o mesmo produto
    }

    @Test
    void existsByCodigoAndIdNotAndAtivoTrue_DeveRetornarTrue_QuandoCodigoExisteEmOutroProduto() {
        // When
        boolean existe = produtoRepository.existsByCodigoAndIdNotAndAtivoTrue("SMART001", produto2.getId());

        // Then
        assertTrue(existe); // Código existe em produto1, mas estamos verificando produto2
    }
}

