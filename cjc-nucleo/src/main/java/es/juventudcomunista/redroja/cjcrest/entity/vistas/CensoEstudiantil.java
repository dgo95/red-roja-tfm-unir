package es.juventudcomunista.redroja.cjcrest.entity.vistas;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "censo_estudiantil")
public class CensoEstudiantil {

    @Id
    @Column(name = "id")
    private Integer id;
    private String militanteId;

    @Column(name = "numero_carnet")
    private String numeroCarnet;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "apellido2")
    private String apellido2;

    @Column(name = "nivel_educativo")
    private String nivelEducativo;

    @Column(name = "subdivision_nivel_educativo")
    private String subdivisionNivelEducativo;

    @Column(name = "subsubdivision_nivel_educativo")
    private String subsubdivisionNivelEducativo;

    @Column(name = "nombre_estudio")
    private String nombreEstudio;

    @Column(name = "centro_estudios")
    private String centroEstudios;

    @Column(name = "anho_finalizacion")
    private Integer anhoFinalizacion;

    @Column(name = "sindicato_estudiantil")
    private String sindicatoEstudiantil;

    private boolean premilitante;
}