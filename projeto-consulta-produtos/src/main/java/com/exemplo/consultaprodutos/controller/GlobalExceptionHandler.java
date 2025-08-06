package com.exemplo.consultaprodutos.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler global para tratamento de exceções da aplicação.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Trata exceções de validação de campos.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse errorResponse = new ErrorResponse(
                "Erro de validação",
                "Os dados fornecidos são inválidos",
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                errors
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Trata exceções de runtime (regras de negócio).
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "Erro de negócio",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                null
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Trata exceções gerais não capturadas.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "Erro interno do servidor",
                "Ocorreu um erro inesperado. Tente novamente mais tarde.",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now(),
                null
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Classe para padronizar respostas de erro.
     */
    public static class ErrorResponse {
        private String titulo;
        private String mensagem;
        private int status;
        private LocalDateTime timestamp;
        private Map<String, String> detalhes;

        public ErrorResponse(String titulo, String mensagem, int status, LocalDateTime timestamp, Map<String, String> detalhes) {
            this.titulo = titulo;
            this.mensagem = mensagem;
            this.status = status;
            this.timestamp = timestamp;
            this.detalhes = detalhes;
        }

        // Getters e Setters
        public String getTitulo() {
            return titulo;
        }

        public void setTitulo(String titulo) {
            this.titulo = titulo;
        }

        public String getMensagem() {
            return mensagem;
        }

        public void setMensagem(String mensagem) {
            this.mensagem = mensagem;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }

        public Map<String, String> getDetalhes() {
            return detalhes;
        }

        public void setDetalhes(Map<String, String> detalhes) {
            this.detalhes = detalhes;
        }
    }
}

