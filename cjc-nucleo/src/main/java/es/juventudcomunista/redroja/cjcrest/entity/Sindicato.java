package es.juventudcomunista.redroja.cjcrest.entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sindicato")
@Builder
public class Sindicato {

    @Id
    private Integer id;

    @Column(length = 100, nullable = false)
    private String nombre;

    @Column(length = 20)
    private String telefono;

    @Column(length = 100)
    private String email;

    @Column(length = 200)
    private String web;

    @Column(name = "numero_afiliados")
    private Integer numeroAfiliados;
}

