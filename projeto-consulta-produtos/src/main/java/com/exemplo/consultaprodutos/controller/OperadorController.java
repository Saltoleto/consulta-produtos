package com.exemplo.consultaprodutos.controller;

import com.exemplo.consultaprodutos.dto.request.OperadorRequestDTO;
import com.exemplo.consultaprodutos.dto.response.OperadorResponseDTO;
import com.exemplo.consultaprodutos.dto.response.ProdutoResponseDTO;
import com.exemplo.consultaprodutos.service.OperadorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para operações relacionadas a Operador.
 */
@RestController
@RequestMapping("/api/operadores")
@CrossOrigin(origins = "*")
public class OperadorController {

    private final OperadorService operadorService;

    @Autowired
    public OperadorController(OperadorService operadorService) {
        this.operadorService = operadorService;
    }

    /**
     * Cria um novo operador.
     * POST /api/operadores
     */
    @PostMapping
    public ResponseEntity<OperadorResponseDTO> criarOperador(@Valid @RequestBody OperadorRequestDTO requestDTO) {
        OperadorResponseDTO response = operadorService.criarOperador(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Busca operador por ID.
     * GET /api/operadores/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<OperadorResponseDTO> buscarPorId(@PathVariable Long id) {
        OperadorResponseDTO response = operadorService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Lista todos os operadores ativos.
     * GET /api/operadores
     */
    @GetMapping
    public ResponseEntity<List<OperadorResponseDTO>> listarTodos(
            @RequestParam(required = false) String nome) {
        
        List<OperadorResponseDTO> response;
        if (nome != null && !nome.trim().isEmpty()) {
            response = operadorService.buscarPorNome(nome.trim());
        } else {
            response = operadorService.listarTodos();
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Lista operadores com paginação.
     * GET /api/operadores/paginado
     */
    @GetMapping("/paginado")
    public ResponseEntity<Page<OperadorResponseDTO>> listarComPaginacao(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) String nome) {
        
        Page<OperadorResponseDTO> response;
        if (nome != null && !nome.trim().isEmpty()) {
            response = operadorService.buscarPorNome(nome.trim(), pageable);
        } else {
            response = operadorService.listarComPaginacao(pageable);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * ENDPOINT PRINCIPAL: Busca produtos associados a um operador.
     * GET /api/operadores/{id}/produtos
     */
    @GetMapping("/{id}/produtos")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarProdutosPorOperador(
            @PathVariable Long id,
            @RequestParam(required = false) String categoria) {
        
        List<ProdutoResponseDTO> response;
        if (categoria != null && !categoria.trim().isEmpty()) {
            response = operadorService.buscarProdutosPorOperadorECategoria(id, categoria.trim());
        } else {
            response = operadorService.buscarProdutosPorOperador(id);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * ENDPOINT PRINCIPAL: Busca produtos associados a um operador com paginação.
     * GET /api/operadores/{id}/produtos/paginado
     */
    @GetMapping("/{id}/produtos/paginado")
    public ResponseEntity<Page<ProdutoResponseDTO>> buscarProdutosPorOperadorComPaginacao(
            @PathVariable Long id,
            @PageableDefault(size = 20) Pageable pageable) {
        
        Page<ProdutoResponseDTO> response = operadorService.buscarProdutosPorOperadorComPaginacao(id, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Atualiza um operador.
     * PUT /api/operadores/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<OperadorResponseDTO> atualizarOperador(
            @PathVariable Long id,
            @Valid @RequestBody OperadorRequestDTO requestDTO) {
        
        OperadorResponseDTO response = operadorService.atualizarOperador(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Remove operador (soft delete).
     * DELETE /api/operadores/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerOperador(@PathVariable Long id) {
        operadorService.removerOperador(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Lista operadores que possuem produtos.
     * GET /api/operadores/com-produtos
     */
    @GetMapping("/com-produtos")
    public ResponseEntity<List<OperadorResponseDTO>> listarOperadoresComProdutos() {
        List<OperadorResponseDTO> response = operadorService.listarOperadoresComProdutos();
        return ResponseEntity.ok(response);
    }

    /**
     * Busca operador por email.
     * GET /api/operadores/email/{email}
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<OperadorResponseDTO> buscarPorEmail(@PathVariable String email) {
        OperadorResponseDTO response = operadorService.buscarPorEmail(email);
        return ResponseEntity.ok(response);
    }
}

