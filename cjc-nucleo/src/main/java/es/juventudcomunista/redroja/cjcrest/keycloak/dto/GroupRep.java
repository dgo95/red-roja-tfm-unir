package es.juventudcomunista.redroja.cjcrest.keycloak.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)   // 👈  ¡la clave!
public class GroupRep {
    private String id;
    private String name;
}
