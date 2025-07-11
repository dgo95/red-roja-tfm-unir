package es.juventudcomunista.redroja.cjcrest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PromocionarMilitanteInput {
    @NotBlank
    private String numeroCarnet;
    @Min(5)
    private Integer cuota;
    @NotNull
    private Boolean activar;
}
