package es.juventudcomunista.redroja.cjcrest.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IdiomaConocidoDTO {
    private Integer id;
    private String nombre;
}
