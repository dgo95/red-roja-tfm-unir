package es.juventudcomunista.redroja.cjcrest.dto;

import lombok.Data;

@Data
public class InvitadoDTO {
    private Integer id;
    private String nombre;
    private String email;
    private boolean esMilitante;
}
