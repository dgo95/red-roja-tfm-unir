package es.juventudcomunista.redroja.cjcrest.entity;

import es.juventudcomunista.redroja.cjcrest.enums.ExisteOrganoRepresentacionTrabajadores;
import lombok.*;

import java.time.LocalDate;
import jakarta.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "contrato")
public class Contrato {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sector_laboral")
    private String sectorLaboral;

    @Column(name = "profesion")
    private String profesion;

    @Column(name = "convenio")
    private String convenio;

    private String nombreCentroTrabajo;
    private Integer numeroTrabajadoresCentroTrabajo;
    private String empresa;
    private Integer numeroTrabajadoresEmpresa;
    
    @Column
    private String direccionTrabajo;

    @ManyToOne
    @JoinColumn(name = "tipo_contrato_id", referencedColumnName = "id")
    private TipoContrato tipoContrato;

    @ManyToOne
    @JoinColumn(name = "modalidad_trabajo_id", referencedColumnName = "id")
    private ModalidadTrabajo modalidadTrabajo;

    @ManyToOne
    @JoinColumn(name = "actividad_economica_id", referencedColumnName = "id")
    private ActividadEconomica actividadEconomica;

    @Enumerated(EnumType.STRING)
    @Column(name = "existe_organo_representacion_trabajadores")
    private ExisteOrganoRepresentacionTrabajadores existeOrganoRepresentacionTrabajadores;

    @Column(name = "participa_organo_representacion", nullable = false)
    private Boolean participaOrganoRepresentacion;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @ManyToOne
    @JoinColumn(name = "militante_id", referencedColumnName = "id", nullable = false)
    private Militante militante;
}
