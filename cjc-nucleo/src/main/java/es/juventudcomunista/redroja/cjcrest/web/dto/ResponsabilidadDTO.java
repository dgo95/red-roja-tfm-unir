package es.juventudcomunista.redroja.cjcrest.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponsabilidadDTO {
    private String clave;
    private String valor;
}
