package es.juventudcomunista.redroja.cjcrest.entity.vistas;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "censo_laboral_sindical")
public class CensoLaboralSindical {

    @Id
    @Column(name = "id")
    private Integer id;
    private String militanteId;

    private String colectivo;
    private String numeroCarnet;
    private String nombre;
    private String apellido;
    private String apellido2;
    private String actividadEconomica;
    private String profesion;
    private String nombreEmpresa;
    private Integer numeroTrabajadoresEmpresa;
    private String nombreCentroTrabajo;
    private Integer numeroTrabajadoresCentroTrabajo;
    private String existeOrganoRepresentacionTrabajadores;
    private String participaOrganoRepresentacion;
    private String tipoContrato;
    private Integer antiguedad;
    private String sindicato;
    private String federacion;
    private String cargo;
    private String participaAreaJuventud;
    private boolean premilitante;
}

