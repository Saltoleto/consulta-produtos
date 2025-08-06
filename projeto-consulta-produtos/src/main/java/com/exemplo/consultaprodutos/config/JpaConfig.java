package com.exemplo.consultaprodutos.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuração JPA para habilitar auditoria automática
 * e configurar repositórios.
 */
@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.exemplo.consultaprodutos.repository")
public class JpaConfig {
    // Configuração automática do Spring Boot
    // A auditoria será aplicada automaticamente nas entidades
    // que estendem BaseEntity e usam @EntityListeners(AuditingEntityListener.class)
}

