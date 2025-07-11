package es.juventudcomunista.redroja.cjcrest.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import es.juventudcomunista.redroja.cjcrest.enums.ChangeType;
import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inventario_historial")
@Builder
public class InventarioHistorial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "material_inventario_id", nullable = false)
    @JsonIgnore
    private MaterialInventario materialInventario;

    @Enumerated(EnumType.STRING)
    @Column(name = "change_type", nullable = false)
    private ChangeType changeType;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "change_date", nullable = false)
    private LocalDateTime changeDate = LocalDateTime.now();

    @Column(name = "description")
    private String description;
}
