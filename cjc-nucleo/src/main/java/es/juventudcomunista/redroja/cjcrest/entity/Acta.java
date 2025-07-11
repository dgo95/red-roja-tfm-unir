package es.juventudcomunista.redroja.cjcrest.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
public class Acta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
    
    @PrePersist
    public void insertar() {
        var ahora = LocalDateTime.now();
        setFechaCreacion(ahora);
        setFechaModificacion(ahora);
    }
    
    @PreUpdate
    public void actualiza() {
        var ahora = LocalDateTime.now();
        setFechaModificacion(ahora);
    }
}
