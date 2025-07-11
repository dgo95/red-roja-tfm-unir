package es.juventudcomunista.redroja.cjcrest.web.dto;

import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
@Builder
public class DatosSindicacionDTO {
    
    @NotNull(message = "El campo 'sindicato' no puede ser nulo")
    private Integer sindicato;
    
    @NotNull(message = "El campo 'federacion' no puede ser nulo")
    private Integer federacion;
    
    private String cargo;
    
    @NotNull(message = "El campo 'participaAreaJuventud' no puede ser nulo")
    private Boolean participaAreaJuventud;
    
    private String sindicatoOtros;
    private String federacionOtros;
}

