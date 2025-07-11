package es.juventudcomunista.redroja.cjcrest.entity;

import es.juventudcomunista.redroja.cjcrest.enums.NivelIdioma;
import lombok.*;
import jakarta.persistence.*;
@Entity
@Table(name = "militante_idioma")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"militante"})
@Builder
public class MilitanteIdioma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "militante_id", nullable = false)
    private Militante militante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idioma_id", nullable = false)
    private IdiomaConocido idiomaConocido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelIdioma nivel;
}
