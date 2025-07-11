package es.juventudcomunista.redroja.cjcrest.entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "direccion")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Direccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "codigo_postal", nullable = true)
    private String codigoPostal;

    @ManyToOne
    @JoinColumn(name = "municipio_id")
    private Municipio municipio;

    @Column(name = "direccion", nullable = false)
    private String direccion;
}
