package com.exemplo.consultaprodutos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Classe principal da aplicação Sistema de Consulta de Produtos.
 * 
 * Esta aplicação implementa um sistema para consulta de produtos associados
 * a operadores e usuários associados a produtos, seguindo as melhores
 * práticas de desenvolvimento com Spring Boot.
 * 
 * Funcionalidades principais:
 * - Consulta de produtos por operador
 * - Consulta de usuários por produto
 * - CRUD completo para todas as entidades
 * - Gerenciamento de associações
 * - Paginação e filtros
 * - Soft delete e auditoria
 * 
 * @author Sistema de Consulta de Produtos
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
public class ConsultaProdutosApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsultaProdutosApplication.class, args);
        
        System.out.println("\n" +
            "=================================================================\n" +
            "    Sistema de Consulta de Produtos - INICIADO COM SUCESSO!     \n" +
            "=================================================================\n" +
            "                                                                 \n" +
            "  🚀 Aplicação rodando em: http://localhost:8080                \n" +
            "  📊 Console H2: http://localhost:8080/h2-console               \n" +
            "                                                                 \n" +
            "  📋 Endpoints principais:                                       \n" +
            "  • GET /api/operadores/{id}/produtos - Produtos do operador    \n" +
            "  • GET /api/produtos/{id}/usuarios - Usuários do produto       \n" +
            "                                                                 \n" +
            "  📚 Documentação completa disponível no README.md              \n" +
            "=================================================================\n"
        );
    }
}

