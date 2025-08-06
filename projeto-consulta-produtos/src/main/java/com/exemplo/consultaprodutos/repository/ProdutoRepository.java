package com.exemplo.consultaprodutos.repository;

import com.exemplo.consultaprodutos.entity.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repositório para operações de dados da entidade Produto.
 */
@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    /**
     * Busca produto por código (apenas ativos).
     */
    Optional<Produto> findByCodigoAndAtivoTrue(String codigo);

    /**
     * Busca todos os produtos ativos.
     */
    List<Produto> findByAtivoTrueOrderByNome();

    /**
     * Busca produtos ativos com paginação.
     */
    Page<Produto> findByAtivoTrueOrderByNome(Pageable pageable);

    /**
     * Busca produtos por nome (busca parcial, case-insensitive).
     */
    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%')) ORDER BY p.nome")
    List<Produto> findByNomeContainingIgnoreCaseAndAtivoTrue(@Param("nome") String nome);

    /**
     * Busca produtos por categoria.
     */
    List<Produto> findByCategoriaAndAtivoTrueOrderByNome(String categoria);

    /**
     * Busca produtos por faixa de preço.
     */
    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND p.preco BETWEEN :precoMin AND :precoMax ORDER BY p.preco")
    List<Produto> findByPrecoRange(@Param("precoMin") BigDecimal precoMin, @Param("precoMax") BigDecimal precoMax);

    /**
     * Verifica se existe produto com o código informado (excluindo o próprio produto).
     */
    @Query("SELECT COUNT(p) > 0 FROM Produto p WHERE p.codigo = :codigo AND p.id != :id AND p.ativo = true")
    boolean existsByCodigoAndIdNotAndAtivoTrue(@Param("codigo") String codigo, @Param("id") Long id);

    /**
     * Verifica se existe produto com o código informado.
     */
    boolean existsByCodigoAndAtivoTrue(String codigo);

    /**
     * CONSULTA PRINCIPAL: Busca produtos associados a um operador específico.
     */
    @Query("SELECT p FROM Produto p " +
           "INNER JOIN p.operadorProdutos op " +
           "WHERE op.operador.id = :operadorId AND op.ativo = true AND p.ativo = true " +
           "ORDER BY p.nome")
    List<Produto> findProdutosByOperadorId(@Param("operadorId") Long operadorId);

    /**
     * CONSULTA PRINCIPAL: Busca produtos associados a um operador específico com paginação.
     */
    @Query("SELECT p FROM Produto p " +
           "INNER JOIN p.operadorProdutos op " +
           "WHERE op.operador.id = :operadorId AND op.ativo = true AND p.ativo = true " +
           "ORDER BY p.nome")
    Page<Produto> findProdutosByOperadorId(@Param("operadorId") Long operadorId, Pageable pageable);

    /**
     * Busca produtos associados a um operador por categoria.
     */
    @Query("SELECT p FROM Produto p " +
           "INNER JOIN p.operadorProdutos op " +
           "WHERE op.operador.id = :operadorId AND p.categoria = :categoria " +
           "AND op.ativo = true AND p.ativo = true " +
           "ORDER BY p.nome")
    List<Produto> findProdutosByOperadorIdAndCategoria(@Param("operadorId") Long operadorId, @Param("categoria") String categoria);

    /**
     * Busca produtos que possuem usuários associados.
     */
    @Query("SELECT DISTINCT p FROM Produto p " +
           "INNER JOIN p.usuarioProdutos up " +
           "WHERE p.ativo = true AND up.ativo = true " +
           "ORDER BY p.nome")
    List<Produto> findProdutosComUsuarios();

    /**
     * Busca produtos que possuem operadores associados.
     */
    @Query("SELECT DISTINCT p FROM Produto p " +
           "INNER JOIN p.operadorProdutos op " +
           "WHERE p.ativo = true AND op.ativo = true " +
           "ORDER BY p.nome")
    List<Produto> findProdutosComOperadores();

    /**
     * Conta quantos operadores um produto possui.
     */
    @Query("SELECT COUNT(op) FROM OperadorProduto op " +
           "WHERE op.produto.id = :produtoId AND op.ativo = true AND op.operador.ativo = true")
    long countOperadoresByProdutoId(@Param("produtoId") Long produtoId);

    /**
     * Conta quantos usuários um produto possui.
     */
    @Query("SELECT COUNT(up) FROM UsuarioProduto up " +
           "WHERE up.produto.id = :produtoId AND up.ativo = true AND up.usuario.ativo = true")
    long countUsuariosByProdutoId(@Param("produtoId") Long produtoId);

    /**
     * Busca todas as categorias distintas de produtos ativos.
     */
    @Query("SELECT DISTINCT p.categoria FROM Produto p WHERE p.ativo = true AND p.categoria IS NOT NULL ORDER BY p.categoria")
    List<String> findDistinctCategorias();
}

