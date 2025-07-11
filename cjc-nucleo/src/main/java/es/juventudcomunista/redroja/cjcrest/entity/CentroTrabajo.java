package es.juventudcomunista.redroja.cjcrest.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;

@Entity
@Table(name = "centro_trabajo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CentroTrabajo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "num_trabajadores")
    private Integer numTrabajadores;

    @ManyToOne
    @JoinColumn(name = "direccion_id", referencedColumnName = "id")
    private Direccion direccion;

    @ManyToOne
    @JoinColumn(name = "empresa_id", referencedColumnName = "id")
    private Empresa empresa;
}


