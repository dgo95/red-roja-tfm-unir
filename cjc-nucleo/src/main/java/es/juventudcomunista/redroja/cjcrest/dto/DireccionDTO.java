package es.juventudcomunista.redroja.cjcrest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DireccionDTO {

    @Pattern(regexp = "^[0-9]{5}$", message = "El código postal debe ser un número de 5 dígitos")
    private String codigoPostal;

    @NotNull(message = "El ID del municipio es obligatorio")
    private Integer municipioId;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;
}

