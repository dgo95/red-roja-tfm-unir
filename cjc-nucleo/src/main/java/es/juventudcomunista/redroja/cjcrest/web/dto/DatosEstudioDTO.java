package es.juventudcomunista.redroja.cjcrest.web.dto;

import lombok.*;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatosEstudioDTO {
    @NotNull(message = "El campo nombreEstudio no puede ser nulo")
    @Size(min = 1, max = 255, message = "El nombre del estudio debe tener entre 1 y 255 caracteres")
    private String nombreEstudios;

    @NotNull(message = "El campo tipoEstudio no puede ser nulo")
    @Min(value = 0, message = "El valor mínimo para tipoEstudio es 1")
    private Integer tipoEstudio;

    @NotNull(message = "El campo subtiposDeEstudio no puede ser nulo")
    @Min(value = 0, message = "El valor mínimo para subtiposDeEstudio es 1")
    private Integer subtipoEstudio;

    @NotNull(message = "El campo nivelEstudio no puede ser nulo")
    @Min(value = 1, message = "El valor mínimo para nivelEstudio es 1")
    private Integer nivelEstudio;

    @NotNull(message = "El campo nombreCentroEstudio no puede ser nulo")
    @Size(min = 1, max = 255, message = "El nombre del centro de estudios debe tener entre 1 y 255 caracteres")
    private String nombreCentroEducativo;

    @NotNull(message = "El campo sindicatoEstudiantil no puede ser nulo")
    private Boolean sindicatoEstudiantil;

    @NotNull(message = "El campo anioFinalizacion no puede ser nulo")
    private Integer anhoFinalizacion;
}
