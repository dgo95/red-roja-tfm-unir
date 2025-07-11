package es.juventudcomunista.redroja.cjccommonutils.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FichaMovilidadDatos {
    private String nombreCompleto;
    private String email;
    private String telefono;
    private String tiempoEstancia;

    // Campos adicionales solicitados
    private String objetoTraslado;
    private String residencia;
    private String frentesTrabajo;
    private String sindicatoEstudiantil;
    private String territorioProcedencia;
    private String responsabilidadActual;
    private String otrasResponsabilidades;
    private String responsabilidadDestacada;
    private String habilidades;
    private String puntosPositivos;
    private String habitosMejorar;
    private String otrasObservaciones;
}