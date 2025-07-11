package es.juventudcomunista.redroja.cjcrest.dto;

import es.juventudcomunista.redroja.cjcrest.enums.ChangeType;
import lombok.Data;

@Data
public class CantidadMaterialDTO {
    private Integer cantidad;
    private ChangeType tipo;
}
