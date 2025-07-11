package es.juventudcomunista.redroja.cjcrest.keycloak.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class KeycloakTokenResponse {
    @JsonProperty("access_token") private String accessToken;
    @JsonProperty("expires_in")  private long  expiresIn;
    @JsonProperty("token_type")  private String tokenType;
}

