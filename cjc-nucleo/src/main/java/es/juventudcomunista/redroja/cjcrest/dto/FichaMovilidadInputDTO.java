package es.juventudcomunista.redroja.cjcrest.dto;

import es.juventudcomunista.redroja.cjcrest.web.dto.ComunidadAutonomaDTO;
import es.juventudcomunista.redroja.cjcrest.web.dto.MunicipioDTO;
import es.juventudcomunista.redroja.cjcrest.web.dto.ProvinciaDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class FichaMovilidadInputDTO {

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    @NotNull(message = "El municipio de destino es obligatorio")
    private int municipio;

    @NotNull(message = "El objeto de traslado es obligatorio")
    private String objetoTraslado;

    // Campos adicionales para las secciones de "Experiencia Trabajo Externo"
    private String frentesTrabajo;
    private String sindicatoEstudiantil;

    // Campos adicionales para las secciones de "Trabajo Interno"
    private String otrasResponsabilidades;
    private String responsabilidadDestacada;
    private String puntosPositivos;
    private String habitosMejorar;

    // Campo para "Otras Observaciones"
    private String otrasObservaciones;

    //inicializar select
    private int provincia;
    private int comunidadAutonoma;
    private List<MunicipioDTO> municipios;
    private List<ProvinciaDTO> provincias;
    private List<ComunidadAutonomaDTO> comunidades;
}

