package com.exemplo.consultaprodutos.repository;

import com.exemplo.consultaprodutos.entity.OperadorProduto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositório para operações de dados da entidade OperadorProduto.
 */
@Repository
public interface OperadorProdutoRepository extends JpaRepository<OperadorProduto, Long> {

    /**
     * Busca associação específica entre operador e produto.
     */
    Optional<OperadorProduto> findByOperadorIdAndProdutoIdAndAtivoTrue(Long operadorId, Long produtoId);

    /**
     * Busca todas as associações ativas de um operador.
     */
    List<OperadorProduto> findByOperadorIdAndAtivoTrueOrderByDataAssociacao(Long operadorId);

    /**
     * Busca todas as associações ativas de um produto.
     */
    List<OperadorProduto> findByProdutoIdAndAtivoTrueOrderByDataAssociacao(Long produtoId);

    /**
     * Busca associações ativas com paginação por operador.
     */
    Page<OperadorProduto> findByOperadorIdAndAtivoTrueOrderByDataAssociacao(Long operadorId, Pageable pageable);

    /**
     * Busca associações ativas com paginação por produto.
     */
    Page<OperadorProduto> findByProdutoIdAndAtivoTrueOrderByDataAssociacao(Long produtoId, Pageable pageable);

    /**
     * Verifica se existe associação ativa entre operador e produto.
     */
    boolean existsByOperadorIdAndProdutoIdAndAtivoTrue(Long operadorId, Long produtoId);

    /**
     * Conta associações ativas de um operador.
     */
    long countByOperadorIdAndAtivoTrue(Long operadorId);

    /**
     * Conta associações ativas de um produto.
     */
    long countByProdutoIdAndAtivoTrue(Long produtoId);

    /**
     * Busca associações por período de criação.
     */
    @Query("SELECT op FROM OperadorProduto op " +
           "WHERE op.ativo = true AND op.dataAssociacao BETWEEN :dataInicio AND :dataFim " +
           "ORDER BY op.dataAssociacao DESC")
    List<OperadorProduto> findByDataAssociacaoBetweenAndAtivoTrue(
            @Param("dataInicio") LocalDateTime dataInicio, 
            @Param("dataFim") LocalDateTime dataFim);

    /**
     * Busca associações mais recentes.
     */
    @Query("SELECT op FROM OperadorProduto op " +
           "WHERE op.ativo = true " +
           "ORDER BY op.dataAssociacao DESC")
    List<OperadorProduto> findRecentAssociations(Pageable pageable);

    /**
     * Busca operadores que possuem mais de X produtos.
     */
    @Query("SELECT op.operador.id, COUNT(op) as total FROM OperadorProduto op " +
           "WHERE op.ativo = true AND op.operador.ativo = true AND op.produto.ativo = true " +
           "GROUP BY op.operador.id " +
           "HAVING COUNT(op) > :minProdutos " +
           "ORDER BY total DESC")
    List<Object[]> findOperadoresComMaisProdutos(@Param("minProdutos") long minProdutos);

    /**
     * Remove logicamente (soft delete) associação específica.
     */
    @Query("UPDATE OperadorProduto op SET op.ativo = false " +
           "WHERE op.operador.id = :operadorId AND op.produto.id = :produtoId")
    void softDeleteByOperadorIdAndProdutoId(@Param("operadorId") Long operadorId, @Param("produtoId") Long produtoId);

    /**
     * Remove logicamente todas as associações de um operador.
     */
    @Query("UPDATE OperadorProduto op SET op.ativo = false WHERE op.operador.id = :operadorId")
    void softDeleteByOperadorId(@Param("operadorId") Long operadorId);

    /**
     * Remove logicamente todas as associações de um produto.
     */
    @Query("UPDATE OperadorProduto op SET op.ativo = false WHERE op.produto.id = :produtoId")
    void softDeleteByProdutoId(@Param("produtoId") Long produtoId);
}

