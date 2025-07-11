package es.juventudcomunista.redroja.cjcrest.entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "federacion")
@Builder
public class Federacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "sindicato_id")
    private Sindicato sindicato;
}

