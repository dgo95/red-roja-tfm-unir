package es.juventudcomunista.redroja.cjcrest.web.dto;

import es.juventudcomunista.redroja.cjcrest.anotatation.ValidaTelefono;
import es.juventudcomunista.redroja.cjcrest.enums.NivelIdioma;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

@Getter
@Setter
public class DatosBasicosDTO {
    @NotEmpty
    @Email
    private String email;
    @NotEmpty
    @ValidaTelefono
    private String telefono;
    @NotNull
    private Boolean esTrabajador;
    @NotNull
    private Boolean esEstudiante;
    @NotNull
    private Boolean sindicado;
    @NotNull
    private Integer municipio;
    
    private String direccion;

    private Map<Integer,String> habilidades;

    private Map<Integer, NivelIdioma> idiomas;
}
