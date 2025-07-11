package es.juventudcomunista.redroja.cjcrest.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "militante_habilidad")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"militante"})
public class MilitanteHabilidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "militante_id", nullable = false)
    private Militante militante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habilidad_id", nullable = false)
    private Habilidad habilidad;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;
}

