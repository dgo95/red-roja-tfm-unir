package es.juventudcomunista.redroja.cjcrest.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Municipios")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Municipio {
    @Id
    @GeneratedValue
    private int id;
    
    
    @ManyToOne
    @JoinColumn(name = "Provincia")
    private Provincia provincia;
    
    @Column(name = "Nombre")
    private String nombre;
}
