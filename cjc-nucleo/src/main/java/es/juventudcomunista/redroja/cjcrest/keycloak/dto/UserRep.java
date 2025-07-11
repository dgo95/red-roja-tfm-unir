package es.juventudcomunista.redroja.cjcrest.keycloak.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 *  DTO de usuario para la Admin REST API de Keycloak.
 *  Solo mapeamos lo que realmente usamos.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRep {

    /* ------------ identificadores básicos ------------- */
    private String id;
    private String username;
    private String email;

    /* ------------ estado y acciones obligatorias ------- */
    private Boolean enabled;          //   true  ⇒ usuario activo
    private Boolean emailVerified;    //   false ⇒ pendiente de verificación

    /** Lista de acciones que Keycloak forzará al primer acceso. */
    @JsonProperty("requiredActions")
    private List<String> requiredActions;

    /* ------------ atributos personalizados ------------- */
    private Map<String, List<String>> attributes;

    /* ------------ datos de perfil ---------------------- */
    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;
}
