package es.juventudcomunista.redroja.cjcrest.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "invitados")
public class Reunion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    @JsonIgnore
    private Duration duracion;
    @JsonProperty("duracion")
    public Integer getDuracionEnHoras() {
        return duracion != null ? (int) duracion.toHours() : null;
    }

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "reunion_id")
    private List<Punto> puntos = new ArrayList<>();


    private String direccion;
    private boolean terminada;
    private boolean aptaPremilitantes;

    @OneToMany(mappedBy = "reunion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Invitado> invitados = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acta_id", referencedColumnName = "id")
    private Acta acta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comite_base_id")
    @JsonIgnore
    private ComiteBase comiteBase;

    @PrePersist
    @PreUpdate
    public void calcularFechaFin() {
        if (fechaInicio != null && duracion != null) {
            fechaFin = fechaInicio.plus(duracion);
        }
    }
}
