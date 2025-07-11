package es.juventudcomunista.redroja.cjcrest.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "security.crypto.field")
public class FieldCryptoProperties {
    /**
     * Clave AES en Base64 (32 bytes para AES-256).
     */
    private String aesKey;
}