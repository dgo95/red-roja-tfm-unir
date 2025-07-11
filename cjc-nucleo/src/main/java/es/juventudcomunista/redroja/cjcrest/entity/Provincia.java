package es.juventudcomunista.redroja.cjcrest.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;

@Entity
@Table(name = "Provincias")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Provincia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "cod_postal", nullable = false)
    private Integer codigoPostal;

    @Column(name = "Nombre", nullable = false, length = 30)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comunidad_autonoma", nullable = false)
    private ComunidadAutonoma comunidadAutonoma;
}
