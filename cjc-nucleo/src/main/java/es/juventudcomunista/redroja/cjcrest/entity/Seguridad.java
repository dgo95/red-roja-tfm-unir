package es.juventudcomunista.redroja.cjcrest.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Entity
@Table(name = "seguridad")
@Getter
@Setter
public class Seguridad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "contrasena")
    private String contrasena;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "militante_id")
    private Militante militante;
}
