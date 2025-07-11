package es.juventudcomunista.redroja.cjcrest.dto;

import lombok.Data;

@Data
public class CensoLaboralSindicalDTO {
    private String id;
    private String colectivo;
    private String numeroCarnet;
    private String nombre;
    private String apellido;
    private String apellido2;
    private String actividadEconomica;
    private String profesion;
    private String nombreEmpresa;
    private Integer numeroTrabajadoresEmpresa;
    private String nombreCentroTrabajo;
    private Integer numeroTrabajadoresCentroTrabajo;
    private String existeOrganoRepresentacionTrabajadores;
    private String participaOrganoRepresentacion;
    private String tipoContrato;
    private Integer antiguedad;
    private String sindicato;
    private String federacion;
    private String cargo;
    private boolean premilitante;
    private String participaAreaJuventud;
}
