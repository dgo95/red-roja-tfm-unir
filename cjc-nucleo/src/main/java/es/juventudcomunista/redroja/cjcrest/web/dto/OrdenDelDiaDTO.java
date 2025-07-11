package es.juventudcomunista.redroja.cjcrest.web.dto;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.Valid;
import java.util.List;

@Getter
@Setter
public class OrdenDelDiaDTO {

    private Long id;
    @Valid
    private List<PuntoDTO> puntos; // Lista de PuntoDTO para evitar referencias circulares
}

