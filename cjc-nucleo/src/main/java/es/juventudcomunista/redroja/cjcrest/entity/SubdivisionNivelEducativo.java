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
@Table(name = "subdivision_nivel_educativo")
public class SubdivisionNivelEducativo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "id_nivel_educativo", referencedColumnName = "id", nullable = false, foreignKey = @ForeignKey(name = "FK_nivel_educativo"))
    private NivelEducativo nivelEducativo;
}
