package es.juventudcomunista.redroja.cjcrest.entity;


import lombok.*;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sindicacion")
@Builder
public class Sindicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String cargo;

    @Column(name = "fecha_sindicacion")
    private LocalDate fechaSindicacion;

    @Column(name = "participa_area_juventud")
    private boolean participaAreaJuventud;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "militante_id", nullable = false)
    private Militante militante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "federacion_id")
    private Federacion federacion;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sindicato_id")
    private Sindicato sindicato;
    
    @Column(nullable = true)
    private String sindicatoOtros;
    @Column(nullable = true)
    private String federacionOtros;

}
