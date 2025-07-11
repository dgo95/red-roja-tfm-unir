package es.juventudcomunista.redroja.cjcgateway.security;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.security.Principal;

@Configuration
public class RateLimitConfig {

    /**
     * Cada bucket se identifica por el usuario autenticado; si no hay token,
     * se utiliza la IP para no regalar barra libre.
     */
    @Bean
    public KeyResolver principalOrIpKeyResolver() {
        return exchange ->
                exchange.getPrincipal()
                        .map(Principal::getName)
                        .switchIfEmpty(
                                Mono.just(exchange.getRequest()
                                        .getRemoteAddress()
                                        .getAddress()
                                        .getHostAddress()));
    }
}

