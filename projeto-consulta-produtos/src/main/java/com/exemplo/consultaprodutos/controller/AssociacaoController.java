package com.exemplo.consultaprodutos.controller;

import com.exemplo.consultaprodutos.dto.request.AssociacaoRequestDTO;
import com.exemplo.consultaprodutos.service.AssociacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para gerenciar associações entre Operador e Produto.
 */
@RestController
@RequestMapping("/api/associacoes")
@CrossOrigin(origins = "*")
public class AssociacaoController {

    private final AssociacaoService associacaoService;

    @Autowired
    public AssociacaoController(AssociacaoService associacaoService) {
        this.associacaoService = associacaoService;
    }

    /**
     * Associa um produto a um operador.
     * POST /api/associacoes/operador-produto
     */
    @PostMapping("/operador-produto")
    public ResponseEntity<Void> associarProdutoAOperador(@Valid @RequestBody AssociacaoRequestDTO requestDTO) {
        associacaoService.associarProdutoAOperador(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Remove associação entre operador e produto.
     * DELETE /api/associacoes/operador-produto/{operadorId}/{produtoId}
     */
    @DeleteMapping("/operador-produto/{operadorId}/{produtoId}")
    public ResponseEntity<Void> removerAssociacaoOperadorProduto(
            @PathVariable Long operadorId,
            @PathVariable Long produtoId) {
        
        associacaoService.removerAssociacaoOperadorProduto(operadorId, produtoId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Verifica se existe associação ativa entre operador e produto.
     * GET /api/associacoes/operador-produto/{operadorId}/{produtoId}/existe
     */
    @GetMapping("/operador-produto/{operadorId}/{produtoId}/existe")
    public ResponseEntity<Boolean> existeAssociacaoOperadorProduto(
            @PathVariable Long operadorId,
            @PathVariable Long produtoId) {
        
        boolean existe = associacaoService.existeAssociacaoOperadorProduto(operadorId, produtoId);
        return ResponseEntity.ok(existe);
    }

    /**
     * Conta associações ativas de um operador.
     * GET /api/associacoes/operador/{operadorId}/count
     */
    @GetMapping("/operador/{operadorId}/count")
    public ResponseEntity<Long> contarAssociacoesOperador(@PathVariable Long operadorId) {
        long count = associacaoService.contarAssociacoesOperador(operadorId);
        return ResponseEntity.ok(count);
    }

    /**
     * Conta associações ativas de um produto (apenas operadores).
     * GET /api/associacoes/produto/{produtoId}/count
     */
    @GetMapping("/produto/{produtoId}/count")
    public ResponseEntity<Long> contarAssociacoesProduto(@PathVariable Long produtoId) {
        long count = associacaoService.contarAssociacoesProduto(produtoId);
        return ResponseEntity.ok(count);
    }
}

