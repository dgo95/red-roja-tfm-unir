package es.juventudcomunista.redroja.cjcdocumentos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaConfig {

    /** El claim que nos interesa es militanteId */
    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(Authentication::isAuthenticated) // evita usuarios anónimos
                .map(Authentication::getPrincipal)
                .filter(Jwt.class::isInstance)            // verificamos que sea un Jwt
                .map(Jwt.class::cast)
                .map(jwt -> jwt.getClaimAsString("militanteId")); // ← claim deseado
    }
}
