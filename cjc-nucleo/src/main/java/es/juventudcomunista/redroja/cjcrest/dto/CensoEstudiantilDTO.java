package es.juventudcomunista.redroja.cjcrest.dto;

import lombok.Data;

@Data
public class CensoEstudiantilDTO {
    private String id;
    private String numeroCarnet;
    private String nombre;
    private String apellido;
    private String apellido2;
    private boolean premilitante;
    private String nivelEducativo;
    private String subdivisionNivelEducativo;
    private String subsubdivisionNivelEducativo;
    private String nombreEstudio;
    private String centroEstudios;
    private Integer anhoFinalizacion;
    private String sindicatoEstudiantil;
}
