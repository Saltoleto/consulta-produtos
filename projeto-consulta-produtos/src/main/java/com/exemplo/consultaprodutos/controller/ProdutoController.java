package com.exemplo.consultaprodutos.controller;

import com.exemplo.consultaprodutos.dto.request.ProdutoRequestDTO;
import com.exemplo.consultaprodutos.dto.response.ProdutoResponseDTO;
import com.exemplo.consultaprodutos.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller REST para operações relacionadas a Produto.
 */
@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "*")
public class ProdutoController {

    private final ProdutoService produtoService;

    @Autowired
    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    /**
     * Cria um novo produto.
     * POST /api/produtos
     */
    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> criarProduto(@Valid @RequestBody ProdutoRequestDTO requestDTO) {
        ProdutoResponseDTO response = produtoService.criarProduto(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Busca produto por ID.
     * GET /api/produtos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable Long id) {
        ProdutoResponseDTO response = produtoService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Lista todos os produtos ativos.
     * GET /api/produtos
     */
    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> listarTodos(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) BigDecimal precoMin,
            @RequestParam(required = false) BigDecimal precoMax) {
        
        List<ProdutoResponseDTO> response;
        
        if (precoMin != null && precoMax != null) {
            response = produtoService.buscarPorFaixaPreco(precoMin, precoMax);
        } else if (categoria != null && !categoria.trim().isEmpty()) {
            response = produtoService.buscarPorCategoria(categoria.trim());
        } else if (nome != null && !nome.trim().isEmpty()) {
            response = produtoService.buscarPorNome(nome.trim());
        } else {
            response = produtoService.listarTodos();
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Lista produtos com paginação.
     * GET /api/produtos/paginado
     */
    @GetMapping("/paginado")
    public ResponseEntity<Page<ProdutoResponseDTO>> listarComPaginacao(
            @PageableDefault(size = 20) Pageable pageable) {
        
        Page<ProdutoResponseDTO> response = produtoService.listarComPaginacao(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Atualiza um produto.
     * PUT /api/produtos/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizarProduto(
            @PathVariable Long id,
            @Valid @RequestBody ProdutoRequestDTO requestDTO) {
        
        ProdutoResponseDTO response = produtoService.atualizarProduto(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Remove produto (soft delete).
     * DELETE /api/produtos/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerProduto(@PathVariable Long id) {
        produtoService.removerProduto(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Lista produtos que possuem operadores.
     * GET /api/produtos/com-operadores
     */
    @GetMapping("/com-operadores")
    public ResponseEntity<List<ProdutoResponseDTO>> listarProdutosComOperadores() {
        List<ProdutoResponseDTO> response = produtoService.listarProdutosComOperadores();
        return ResponseEntity.ok(response);
    }

    /**
     * Busca produto por código.
     * GET /api/produtos/codigo/{codigo}
     */
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorCodigo(@PathVariable String codigo) {
        ProdutoResponseDTO response = produtoService.buscarPorCodigo(codigo);
        return ResponseEntity.ok(response);
    }

    /**
     * Lista todas as categorias distintas.
     * GET /api/produtos/categorias
     */
    @GetMapping("/categorias")
    public ResponseEntity<List<String>> listarCategorias() {
        List<String> response = produtoService.listarCategorias();
        return ResponseEntity.ok(response);
    }

    /**
     * Busca produtos por categoria.
     * GET /api/produtos/categoria/{categoria}
     */
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorCategoria(@PathVariable String categoria) {
        List<ProdutoResponseDTO> response = produtoService.buscarPorCategoria(categoria);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca produtos por faixa de preço.
     * GET /api/produtos/preco
     */
    @GetMapping("/preco")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorFaixaPreco(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        
        List<ProdutoResponseDTO> response = produtoService.buscarPorFaixaPreco(min, max);
        return ResponseEntity.ok(response);
    }
}

