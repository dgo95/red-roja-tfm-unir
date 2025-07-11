package es.juventudcomunista.redroja.cjcrest.entity;

import lombok.*;
import jakarta.persistence.*;

    @Entity
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Table(name = "asignacion_material")
    public class AsignacionMaterial {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        @ManyToOne
        @JoinColumn(name = "material_inventario_id", nullable = false)
        private MaterialInventario materialInventario;

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "militante_id", nullable = true)
        private Militante militante;

        @ManyToOne
        @JoinColumn(name = "comite_base_id", nullable = true)
        private ComiteBase comiteBase;

        @ManyToOne
        @JoinColumn(name = "comite_id", nullable = true)
        private Comite comite;

        @Column(name = "cantidad", nullable = false)
        private Integer cantidad;
    }

