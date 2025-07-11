package es.juventudcomunista.redroja.cjcrest.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ficha_movilidad")
public class FichaMovilidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @ManyToOne
    @JoinColumn(name = "municipio_id")
    private Municipio municipio;

    @Column(name = "objeto_traslado", nullable = false)
    private String objetoTraslado;

    @Column(name = "frentes_trabajo")
    private String frentesTrabajo;

    @Column(name = "sindicato_estudiantil")
    private String sindicatoEstudiantil;

    @Column(name = "otras_responsabilidades")
    private String otrasResponsabilidades;

    @Column(name = "responsabilidad_destacada")
    private String responsabilidadDestacada;

    @Column(name = "puntos_positivos")
    private String puntosPositivos;

    @Column(name = "habitos_mejorar")
    private String habitosMejorar;

    @Column(name = "otras_observaciones")
    private String otrasObservaciones;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "militante_id", referencedColumnName = "id", nullable = false)
    private Militante militante;

    @ManyToOne
    @JoinColumn(name = "comite_responsable_id")
    private Comite comiteResponsable;


}

