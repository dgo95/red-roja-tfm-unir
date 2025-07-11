package es.juventudcomunista.redroja.cjcrest.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import jakarta.persistence.*;

@Entity
@Data
@ToString(exclude = "reunion")
public class Invitado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;
    private String email;
    private boolean esMilitante;
    private String numeroCarnet;

    @ManyToOne
    @JoinColumn(name = "reunion_id")
    @JsonIgnore
    private Reunion reunion;

    @PrePersist
    @PreUpdate
    private void validarMilitante() {
        if (esMilitante && (numeroCarnet == null || numeroCarnet.trim().isEmpty())) {
            throw new IllegalArgumentException("Número de carnet es obligatorio para militantes.");
        }
    }
}
