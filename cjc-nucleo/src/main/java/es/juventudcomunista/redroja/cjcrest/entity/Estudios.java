package es.juventudcomunista.redroja.cjcrest.entity;

import lombok.*;
import jakarta.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "estudios")
public class Estudios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre_estudio", nullable = false)
    private String nombreEstudio;

    private Integer nivelEducativo;
    private Integer subdivisionNivelEducativo;
    private Integer subSubdivisionNivelEducativo;

    @Column(name = "anho_finalizacion")
    private Integer anhoFinalizacion;

    @Column(name = "sindicato_estudiantil")
    private boolean sindicatoEstudiantil;

    @Column(name = "centro_estudios")
    private String centroEstudios;

    @ManyToOne
    @JoinColumn(name = "militante_id", referencedColumnName = "id", nullable = false)
    private Militante militante;
}

