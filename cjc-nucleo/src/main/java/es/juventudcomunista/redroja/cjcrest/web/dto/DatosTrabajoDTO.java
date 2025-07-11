package es.juventudcomunista.redroja.cjcrest.web.dto;

import es.juventudcomunista.redroja.cjcrest.enums.ExisteOrganoRepresentacionTrabajadores;
import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
public class DatosTrabajoDTO {

    @NotNull(message = "La actividad económica no puede ser nula")
    private Integer actividadEconomica;

    @NotNull(message = "El tipo de contrato no puede ser nulo")
    private Integer tipoContrato;

    @NotNull(message = "La modalidad de trabajo no puede ser nula")
    private Integer modalidadTrabajo;

    @NotBlank(message = "El puesto no puede estar en blanco")
    private String profesion;

    @NotNull(message = "Debe especificar si existe un órgano de representación de trabajadores")
    private ExisteOrganoRepresentacionTrabajadores existeOrganoRepresentacion;

    @NotNull(message = "Debe especificar si participa en el órgano de representación de trabajadores")
    private Boolean participaOrganoRepresentacion;

    private String nombreCentroTrabajo;

    private Integer numeroTrabajadoresCentroTrabajo;

    private String direccionCentroTrabajo;

    @NotBlank(message = "El nombre de la empresa no puede estar en blanco")
    private String nombreEmpresa;

    @NotNull(message = "El número de trabajadores no puede ser nulo")
    private Integer numeroTrabajadores;

    @NotNull(message = "La fecha de inicio del contrato no puede ser nula")
    private LocalDate fechaInicioContrato;
}

