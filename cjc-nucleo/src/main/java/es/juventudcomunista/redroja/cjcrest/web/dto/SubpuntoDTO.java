package es.juventudcomunista.redroja.cjcrest.web.dto;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotEmpty;

@Getter
@Setter
public class SubpuntoDTO {

    private Long id;
    @NotEmpty
    private String titulo;
    private String descripcion;
    @NotEmpty
    private Integer orden;
}
