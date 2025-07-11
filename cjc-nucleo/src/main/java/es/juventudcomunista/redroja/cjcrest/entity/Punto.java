package es.juventudcomunista.redroja.cjcrest.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "punto")
@Getter
@Setter
@NoArgsConstructor
public class Punto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reunion_id")
    private Reunion reunion;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "orden")
    private Integer orden;
    
    @Column(name = "descripcion")
    private String descripcion;


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "punto_id")
    private Set<Subpunto> subpuntos;

    @Override
    public String toString() {
        var builder = new StringBuilder(orden + ". ").append(titulo);
        if (descripcion != null && !descripcion.isEmpty()) {
            builder.append(" - ").append(descripcion);
        }
        if (subpuntos != null && !subpuntos.isEmpty()) {
            builder.append("\n").append(
                subpuntos.stream()
                         .map(Subpunto::toString)
                         .collect(Collectors.joining("\n"))
            );
        }
        return builder.toString();
    }
}

