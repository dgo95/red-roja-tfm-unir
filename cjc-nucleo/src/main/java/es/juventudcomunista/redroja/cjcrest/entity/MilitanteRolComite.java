package es.juventudcomunista.redroja.cjcrest.entity;

import jakarta.persistence.*;
import lombok.*;
import es.juventudcomunista.redroja.cjccommonutils.enums.Rol;

@Entity
@Table(name = "militante_rol_comite")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"comiteBase", "comite", "militante"})
@Builder
public class MilitanteRolComite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "militante_id", nullable = false)
    private Militante militante;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "comite_id", nullable = true)
    private Comite comite;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "comite_base_id", nullable = true)
    private ComiteBase comiteBase;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false)
    private Rol rol;
}
