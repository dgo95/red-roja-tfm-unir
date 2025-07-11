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
@Table(name = "empresa")
public class Empresa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(name = "num_trabajadores")
    private Integer numTrabajadores;

    @ManyToOne
    @JoinColumn(name = "direccion_id")
    private Direccion direccion;
}
