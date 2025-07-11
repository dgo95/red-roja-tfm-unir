package es.juventudcomunista.redroja.cjcrest.dto;

import es.juventudcomunista.redroja.cjcrest.enums.ChangeType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InventarioHistorialDTO {
    private String materialInventario;
    private ChangeType changeType;
    private Integer variacion;
    private LocalDateTime changeDate;
    private String description;
}
