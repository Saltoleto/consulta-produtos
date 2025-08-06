package com.exemplo.consultaprodutos.repository;

import com.exemplo.consultaprodutos.entity.UsuarioProduto;
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
 * Repositório para operações de dados da entidade UsuarioProduto.
 */
@Repository
public interface UsuarioProdutoRepository extends JpaRepository<UsuarioProduto, Long> {

    /**
     * Busca associação específica entre usuário e produto.
     */
    Optional<UsuarioProduto> findByUsuarioIdAndProdutoIdAndAtivoTrue(Long usuarioId, Long produtoId);

    /**
     * Busca todas as associações ativas de um usuário.
     */
    List<UsuarioProduto> findByUsuarioIdAndAtivoTrueOrderByDataAssociacao(Long usuarioId);

    /**
     * Busca todas as associações ativas de um produto.
     */
    List<UsuarioProduto> findByProdutoIdAndAtivoTrueOrderByDataAssociacao(Long produtoId);

    /**
     * Busca associações ativas com paginação por usuário.
     */
    Page<UsuarioProduto> findByUsuarioIdAndAtivoTrueOrderByDataAssociacao(Long usuarioId, Pageable pageable);

    /**
     * Busca associações ativas com paginação por produto.
     */
    Page<UsuarioProduto> findByProdutoIdAndAtivoTrueOrderByDataAssociacao(Long produtoId, Pageable pageable);

    /**
     * Verifica se existe associação ativa entre usuário e produto.
     */
    boolean existsByUsuarioIdAndProdutoIdAndAtivoTrue(Long usuarioId, Long produtoId);

    /**
     * Conta associações ativas de um usuário.
     */
    long countByUsuarioIdAndAtivoTrue(Long usuarioId);

    /**
     * Conta associações ativas de um produto.
     */
    long countByProdutoIdAndAtivoTrue(Long produtoId);

    /**
     * Busca associações por período de criação.
     */
    @Query("SELECT up FROM UsuarioProduto up " +
           "WHERE up.ativo = true AND up.dataAssociacao BETWEEN :dataInicio AND :dataFim " +
           "ORDER BY up.dataAssociacao DESC")
    List<UsuarioProduto> findByDataAssociacaoBetweenAndAtivoTrue(
            @Param("dataInicio") LocalDateTime dataInicio, 
            @Param("dataFim") LocalDateTime dataFim);

    /**
     * Busca associações mais recentes.
     */
    @Query("SELECT up FROM UsuarioProduto up " +
           "WHERE up.ativo = true " +
           "ORDER BY up.dataAssociacao DESC")
    List<UsuarioProduto> findRecentAssociations(Pageable pageable);

    /**
     * Busca usuários que possuem mais de X produtos.
     */
    @Query("SELECT up.usuario.id, COUNT(up) as total FROM UsuarioProduto up " +
           "WHERE up.ativo = true AND up.usuario.ativo = true AND up.produto.ativo = true " +
           "GROUP BY up.usuario.id " +
           "HAVING COUNT(up) > :minProdutos " +
           "ORDER BY total DESC")
    List<Object[]> findUsuariosComMaisProdutos(@Param("minProdutos") long minProdutos);

    /**
     * Busca produtos mais populares (com mais usuários).
     */
    @Query("SELECT up.produto.id, COUNT(up) as total FROM UsuarioProduto up " +
           "WHERE up.ativo = true AND up.usuario.ativo = true AND up.produto.ativo = true " +
           "GROUP BY up.produto.id " +
           "ORDER BY total DESC")
    List<Object[]> findProdutosMaisPopulares(Pageable pageable);

    /**
     * Remove logicamente (soft delete) associação específica.
     */
    @Query("UPDATE UsuarioProduto up SET up.ativo = false " +
           "WHERE up.usuario.id = :usuarioId AND up.produto.id = :produtoId")
    void softDeleteByUsuarioIdAndProdutoId(@Param("usuarioId") Long usuarioId, @Param("produtoId") Long produtoId);

    /**
     * Remove logicamente todas as associações de um usuário.
     */
    @Query("UPDATE UsuarioProduto up SET up.ativo = false WHERE up.usuario.id = :usuarioId")
    void softDeleteByUsuarioId(@Param("usuarioId") Long usuarioId);

    /**
     * Remove logicamente todas as associações de um produto.
     */
    @Query("UPDATE UsuarioProduto up SET up.ativo = false WHERE up.produto.id = :produtoId")
    void softDeleteByProdutoId(@Param("produtoId") Long produtoId);

    /**
     * Busca usuários que possuem produtos de uma categoria específica.
     */
    @Query("SELECT up FROM UsuarioProduto up " +
           "INNER JOIN up.produto p " +
           "WHERE up.ativo = true AND up.usuario.ativo = true AND p.ativo = true " +
           "AND p.categoria = :categoria " +
           "ORDER BY up.dataAssociacao DESC")
    List<UsuarioProduto> findByProdutoCategoria(@Param("categoria") String categoria);
}

