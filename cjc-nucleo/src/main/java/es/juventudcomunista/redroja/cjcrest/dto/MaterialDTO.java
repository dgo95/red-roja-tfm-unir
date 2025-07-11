package es.juventudcomunista.redroja.cjcrest.dto;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;

@Data
@Builder
public class MaterialDTO {
    private String nombre;
    private String descripcion;
    private Integer cantidadTotal;
    private HashMap<String, Object> responsables;
}
