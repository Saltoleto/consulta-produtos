package com.exemplo.consultaprodutos.repository;

import com.exemplo.consultaprodutos.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para operações de dados da entidade Usuario.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca usuário por email (apenas ativos).
     */
    Optional<Usuario> findByEmailAndAtivoTrue(String email);

    /**
     * Busca todos os usuários ativos.
     */
    List<Usuario> findByAtivoTrueOrderByNome();

    /**
     * Busca usuários ativos com paginação.
     */
    Page<Usuario> findByAtivoTrueOrderByNome(Pageable pageable);

    /**
     * Busca usuários por nome (busca parcial, case-insensitive).
     */
    @Query("SELECT u FROM Usuario u WHERE u.ativo = true AND LOWER(u.nome) LIKE LOWER(CONCAT('%', :nome, '%')) ORDER BY u.nome")
    List<Usuario> findByNomeContainingIgnoreCaseAndAtivoTrue(@Param("nome") String nome);

    /**
     * Busca usuários por nome com paginação.
     */
    @Query("SELECT u FROM Usuario u WHERE u.ativo = true AND LOWER(u.nome) LIKE LOWER(CONCAT('%', :nome, '%')) ORDER BY u.nome")
    Page<Usuario> findByNomeContainingIgnoreCaseAndAtivoTrue(@Param("nome") String nome, Pageable pageable);

    /**
     * Verifica se existe usuário com o email informado (excluindo o próprio usuário).
     */
    @Query("SELECT COUNT(u) > 0 FROM Usuario u WHERE u.email = :email AND u.id != :id AND u.ativo = true")
    boolean existsByEmailAndIdNotAndAtivoTrue(@Param("email") String email, @Param("id") Long id);

    /**
     * Verifica se existe usuário com o email informado.
     */
    boolean existsByEmailAndAtivoTrue(String email);

    /**
     * CONSULTA PRINCIPAL: Busca usuários associados a um produto específico.
     */
    @Query("SELECT u FROM Usuario u " +
           "INNER JOIN u.usuarioProdutos up " +
           "WHERE up.produto.id = :produtoId AND up.ativo = true AND u.ativo = true " +
           "ORDER BY u.nome")
    List<Usuario> findUsuariosByProdutoId(@Param("produtoId") Long produtoId);

    /**
     * CONSULTA PRINCIPAL: Busca usuários associados a um produto específico com paginação.
     */
    @Query("SELECT u FROM Usuario u " +
           "INNER JOIN u.usuarioProdutos up " +
           "WHERE up.produto.id = :produtoId AND up.ativo = true AND u.ativo = true " +
           "ORDER BY u.nome")
    Page<Usuario> findUsuariosByProdutoId(@Param("produtoId") Long produtoId, Pageable pageable);

    /**
     * Busca usuários que possuem produtos associados.
     */
    @Query("SELECT DISTINCT u FROM Usuario u " +
           "INNER JOIN u.usuarioProdutos up " +
           "WHERE u.ativo = true AND up.ativo = true " +
           "ORDER BY u.nome")
    List<Usuario> findUsuariosComProdutos();

    /**
     * Busca usuários que possuem produtos associados com paginação.
     */
    @Query("SELECT DISTINCT u FROM Usuario u " +
           "INNER JOIN u.usuarioProdutos up " +
           "WHERE u.ativo = true AND up.ativo = true " +
           "ORDER BY u.nome")
    Page<Usuario> findUsuariosComProdutos(Pageable pageable);

    /**
     * Conta quantos produtos um usuário possui.
     */
    @Query("SELECT COUNT(up) FROM UsuarioProduto up " +
           "WHERE up.usuario.id = :usuarioId AND up.ativo = true AND up.produto.ativo = true")
    long countProdutosByUsuarioId(@Param("usuarioId") Long usuarioId);

    /**
     * Busca usuários que possuem um produto específico por categoria.
     */
    @Query("SELECT u FROM Usuario u " +
           "INNER JOIN u.usuarioProdutos up " +
           "INNER JOIN up.produto p " +
           "WHERE p.categoria = :categoria AND up.ativo = true AND u.ativo = true AND p.ativo = true " +
           "ORDER BY u.nome")
    List<Usuario> findUsuariosByProdutoCategoria(@Param("categoria") String categoria);

    /**
     * Busca usuários que possuem produtos de um operador específico.
     */
    @Query("SELECT DISTINCT u FROM Usuario u " +
           "INNER JOIN u.usuarioProdutos up " +
           "INNER JOIN up.produto p " +
           "INNER JOIN p.operadorProdutos op " +
           "WHERE op.operador.id = :operadorId " +
           "AND up.ativo = true AND u.ativo = true AND p.ativo = true AND op.ativo = true " +
           "ORDER BY u.nome")
    List<Usuario> findUsuariosByOperadorId(@Param("operadorId") Long operadorId);
}

