package com.exemplo.consultaprodutos.controller;

import com.exemplo.consultaprodutos.dto.request.UsuarioRequestDTO;
import com.exemplo.consultaprodutos.dto.response.ProdutoResponseDTO;
import com.exemplo.consultaprodutos.dto.response.UsuarioResponseDTO;
import com.exemplo.consultaprodutos.service.UsuarioService;
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
 * Controller REST para operações relacionadas a Usuario.
 */
@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Cria um novo usuário.
     * POST /api/usuarios
     */
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criarUsuario(@Valid @RequestBody UsuarioRequestDTO requestDTO) {
        UsuarioResponseDTO response = usuarioService.criarUsuario(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Busca usuário por ID.
     * GET /api/usuarios/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        UsuarioResponseDTO response = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Lista todos os usuários ativos.
     * GET /api/usuarios
     */
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos(
            @RequestParam(required = false) String nome) {
        
        List<UsuarioResponseDTO> response;
        if (nome != null && !nome.trim().isEmpty()) {
            response = usuarioService.buscarPorNome(nome.trim());
        } else {
            response = usuarioService.listarTodos();
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Lista usuários com paginação.
     * GET /api/usuarios/paginado
     */
    @GetMapping("/paginado")
    public ResponseEntity<Page<UsuarioResponseDTO>> listarComPaginacao(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) String nome) {
        
        Page<UsuarioResponseDTO> response;
        if (nome != null && !nome.trim().isEmpty()) {
            response = usuarioService.buscarPorNome(nome.trim(), pageable);
        } else {
            response = usuarioService.listarComPaginacao(pageable);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Busca produtos associados a um usuário.
     * GET /api/usuarios/{id}/produtos
     */
    @GetMapping("/{id}/produtos")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarProdutosPorUsuario(@PathVariable Long id) {
        List<ProdutoResponseDTO> response = usuarioService.buscarProdutosPorUsuario(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Atualiza um usuário.
     * PUT /api/usuarios/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDTO requestDTO) {
        
        UsuarioResponseDTO response = usuarioService.atualizarUsuario(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Remove usuário (soft delete).
     * DELETE /api/usuarios/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerUsuario(@PathVariable Long id) {
        usuarioService.removerUsuario(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Lista usuários que possuem produtos.
     * GET /api/usuarios/com-produtos
     */
    @GetMapping("/com-produtos")
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuariosComProdutos() {
        List<UsuarioResponseDTO> response = usuarioService.listarUsuariosComProdutos();
        return ResponseEntity.ok(response);
    }

    /**
     * Lista usuários que possuem produtos com paginação.
     * GET /api/usuarios/com-produtos/paginado
     */
    @GetMapping("/com-produtos/paginado")
    public ResponseEntity<Page<UsuarioResponseDTO>> listarUsuariosComProdutosComPaginacao(
            @PageableDefault(size = 20) Pageable pageable) {
        
        Page<UsuarioResponseDTO> response = usuarioService.listarUsuariosComProdutosComPaginacao(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca usuário por email.
     * GET /api/usuarios/email/{email}
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorEmail(@PathVariable String email) {
        UsuarioResponseDTO response = usuarioService.buscarPorEmail(email);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca usuários que possuem produtos de uma categoria específica.
     * GET /api/usuarios/categoria/{categoria}
     */
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<UsuarioResponseDTO>> buscarUsuariosPorProdutoCategoria(@PathVariable String categoria) {
        List<UsuarioResponseDTO> response = usuarioService.buscarUsuariosPorProdutoCategoria(categoria);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca usuários que possuem produtos de um operador específico.
     * GET /api/usuarios/operador/{operadorId}
     */
    @GetMapping("/operador/{operadorId}")
    public ResponseEntity<List<UsuarioResponseDTO>> buscarUsuariosPorOperador(@PathVariable Long operadorId) {
        List<UsuarioResponseDTO> response = usuarioService.buscarUsuariosPorOperador(operadorId);
        return ResponseEntity.ok(response);
    }

    /**
     * Conta quantos produtos um usuário possui.
     * GET /api/usuarios/{id}/produtos/count
     */
    @GetMapping("/{id}/produtos/count")
    public ResponseEntity<Long> contarProdutosPorUsuario(@PathVariable Long id) {
        Long count = usuarioService.contarProdutosPorUsuario(id);
        return ResponseEntity.ok(count);
    }
}

