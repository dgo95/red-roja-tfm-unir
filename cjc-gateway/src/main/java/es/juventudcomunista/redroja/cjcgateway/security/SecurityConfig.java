package es.juventudcomunista.redroja.cjcgateway.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;


import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Slf4j
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         CorsConfigurationSource corsSource) {
        return http
                // 1) CORS habilitado usando nuestro CorsConfigurationSource reactivo
                .cors(cors -> cors.configurationSource(corsSource))

                // 2) Deshabilita CSRF (no hay sesiones)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                // 3) Autorización por rutas
                .authorizeExchange(authz -> authz.anyExchange().authenticated())

                // 4) Configura Resource Server para JWT
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                // Adaptamos el converter síncrono al que pide el spec reactivo
                                .jwtAuthenticationConverter(
                                        new ReactiveJwtAuthenticationConverterAdapter(jwtAuthConverter())
                                )
                        )
                )
                .build();
    }

    /**
     * Converter síncrono para extraer roles/scopes de Keycloak
     * que luego adaptamos a reactivo con ReactiveJwtAuthenticationConverterAdapter.
     */
    private Converter<Jwt,AbstractAuthenticationToken> jwtAuthConverter() {
        JwtGrantedAuthoritiesConverter scopesConverter = new JwtGrantedAuthoritiesConverter();
        scopesConverter.setAuthorityPrefix("");      // sin prefijo "SCOPE_"
        // scopesConverter.setAuthoritiesClaimName("scope"); // si quieres claim distinto

        // Converter que crea el AuthenticationToken
        return jwt -> {
            var authorities = scopesConverter.convert(jwt);
            return new org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken(jwt, authorities);
        };
    }

    /**
     * Bean reactivo para CORS. Usamos UrlBasedCorsConfigurationSource de WebFlux.
     */
    /**
     * Configuración CORS para desarrollo local.
     * – localhost sólo puerto 4200
     * – 127.0.0.1 en cualquier puerto
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var corsConfig = new CorsConfiguration();

        corsConfig.setAllowedOriginPatterns(
                List.of(
                        "https://localhost:4200",   // sólo este puerto
                        "http://127.0.0.1:*",      // cualquier puerto
                        "https://127.0.0.1:*"      // opcional si usas HTTPS
                )
        );
        corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS","PATCH"));
        corsConfig.setAllowedHeaders(List.of("*"));
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;   // Bean reactivo que ahora sí inyectará Spring
    }
}
