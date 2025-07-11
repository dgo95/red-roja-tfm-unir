package es.juventudcomunista.redroja.cjcrest.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class MaterialInputDTO {
    private String nombre;
    private String descripcion;
    private Map<String, Integer> responsables;
}
