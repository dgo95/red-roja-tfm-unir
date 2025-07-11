package es.juventudcomunista.redroja.cjcrest.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="subsubdivision_nivel_educativo ")
public class SubsubdivisionNivelEducativo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "id_subdivision_nivel_educativo")
    private SubdivisionNivelEducativo subdivisionNivelEducativo;
}

