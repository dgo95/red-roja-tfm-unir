package es.juventudcomunista.redroja.cjcrest.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
public class CensoGeneralDTO {
    private String id;
    private String numeroCarnet;
    private String nombre;
    private String apellido;
    private String apellido2;
    private String sexo;
    private LocalDate fechaNacimiento;
    private boolean premilitante;
    private String estudiante;
    private String centroEstudios;
    private String sindicatoEstudiantil;
    private String trabajador;
    private String sectorLaboral;
    private String sindicatoLaboral;
    private String correoElectronico;
    private String telefono;
    private String idiomas;

    private Map<String, String> habilidades;
    private Boolean fichaMovilidad;
}

