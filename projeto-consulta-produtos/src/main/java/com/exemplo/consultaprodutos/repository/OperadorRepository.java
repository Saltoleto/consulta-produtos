package com.exemplo.consultaprodutos.repository;

import com.exemplo.consultaprodutos.entity.Operador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para operações de dados da entidade Operador.
 */
@Repository
public interface OperadorRepository extends JpaRepository<Operador, Long> {

    /**
     * Busca operador por email (apenas ativos).
     */
    Optional<Operador> findByEmailAndAtivoTrue(String email);

    /**
     * Busca todos os operadores ativos.
     */
    List<Operador> findByAtivoTrueOrderByNome();

    /**
     * Busca operadores ativos com paginação.
     */
    Page<Operador> findByAtivoTrueOrderByNome(Pageable pageable);

    /**
     * Busca operadores por nome (busca parcial, case-insensitive).
     */
    @Query("SELECT o FROM Operador o WHERE o.ativo = true AND LOWER(o.nome) LIKE LOWER(CONCAT('%', :nome, '%')) ORDER BY o.nome")
    List<Operador> findByNomeContainingIgnoreCaseAndAtivoTrue(@Param("nome") String nome);

    /**
     * Busca operadores por nome com paginação.
     */
    @Query("SELECT o FROM Operador o WHERE o.ativo = true AND LOWER(o.nome) LIKE LOWER(CONCAT('%', :nome, '%')) ORDER BY o.nome")
    Page<Operador> findByNomeContainingIgnoreCaseAndAtivoTrue(@Param("nome") String nome, Pageable pageable);

    /**
     * Verifica se existe operador com o email informado (excluindo o próprio operador).
     */
    @Query("SELECT COUNT(o) > 0 FROM Operador o WHERE o.email = :email AND o.id != :id AND o.ativo = true")
    boolean existsByEmailAndIdNotAndAtivoTrue(@Param("email") String email, @Param("id") Long id);

    /**
     * Verifica se existe operador com o email informado.
     */
    boolean existsByEmailAndAtivoTrue(String email);

    /**
     * Busca operadores que possuem produtos associados.
     */
    @Query("SELECT DISTINCT o FROM Operador o " +
           "INNER JOIN o.operadorProdutos op " +
           "WHERE o.ativo = true AND op.ativo = true " +
           "ORDER BY o.nome")
    List<Operador> findOperadoresComProdutos();

    /**
     * Busca operadores que possuem produtos associados com paginação.
     */
    @Query("SELECT DISTINCT o FROM Operador o " +
           "INNER JOIN o.operadorProdutos op " +
           "WHERE o.ativo = true AND op.ativo = true " +
           "ORDER BY o.nome")
    Page<Operador> findOperadoresComProdutos(Pageable pageable);

    /**
     * Conta quantos produtos um operador possui.
     */
    @Query("SELECT COUNT(op) FROM OperadorProduto op " +
           "WHERE op.operador.id = :operadorId AND op.ativo = true AND op.produto.ativo = true")
    long countProdutosByOperadorId(@Param("operadorId") Long operadorId);
}

