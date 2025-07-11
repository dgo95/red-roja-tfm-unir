package es.juventudcomunista.redroja.cjcrest.entity.vistas;


import es.juventudcomunista.redroja.cjcrest.security.crypto.AesGcmConverter;
import lombok.Data;

import java.time.LocalDate;

import jakarta.persistence.*;

@Data
@Entity
@Table(name = "censo_general")
public class CensoGeneral {

    @Id
    @Column(name = "id")
    private Integer id;

    private String militanteId;
    private String numeroCarnet;
    private String nombre;
    private String apellido;
    private String apellido2;
    private String sexo;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    private String estudiante;

    @Column(name = "centro_estudios")
    private String centroEstudios;

    @Column(name = "sindicato_estudiantil")
    private String sindicatoEstudiantil;

    private String trabajador;

    @Column(name = "sector_laboral")
    private String sectorLaboral;

    @Column(name = "sindicato_laboral")
    private String sindicatoLaboral;

    @Convert(converter = AesGcmConverter.class)
    @Column(name = "correo_electronico")
    private String correoElectronico;

    @Convert(converter = AesGcmConverter.class)
    private String telefono;

    private String idiomas;

    private boolean premilitante;
}

