package es.juventudcomunista.redroja.cjcrest.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "contactos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contacto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    private Boolean estudiante;

    private Boolean trabajador;

    private String telefono;

    private String email;

    // Relación muchos a uno con Municipio
    @ManyToOne
    @JoinColumn(name = "municipio_id", nullable = false)
    private Municipio municipio;

    @Column(name = "situacion_origen")
    private String situacionOrigen;

    @Column(name = "estado_actual")
    private String estadoActual;

    // Relación muchos a uno con Militante para encargado de seguimiento
    @ManyToOne
    @JoinColumn(name = "militante_id", nullable = false)
    private Militante encargadoSeguimiento;

    @Column(name = "proxima_tarea")
    private String proximaTarea;
}
