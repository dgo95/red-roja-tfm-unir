package es.juventudcomunista.redroja.cjcrest.web.dto;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.Set;

@Getter
@Setter
public class PuntoDTO {

    private Long id;
    @NotEmpty
    private String titulo;
    @NotEmpty
    private Integer orden;
    private String descripcion;
    @Valid
    private Set<SubpuntoDTO> subpuntos; // Similar a PuntoDTO
}

