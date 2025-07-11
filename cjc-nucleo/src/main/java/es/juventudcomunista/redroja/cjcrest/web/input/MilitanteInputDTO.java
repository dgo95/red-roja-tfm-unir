package es.juventudcomunista.redroja.cjcrest.web.input;

import es.juventudcomunista.redroja.cjcrest.dto.DireccionDTO;
import es.juventudcomunista.redroja.cjcrest.enums.ExisteOrganoRepresentacionTrabajadores;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import es.juventudcomunista.redroja.cjccommonutils.enums.Idioma;
import es.juventudcomunista.redroja.cjccommonutils.enums.Organizacion;
import es.juventudcomunista.redroja.cjccommonutils.enums.Rol;
import es.juventudcomunista.redroja.cjccommonutils.enums.Sexo;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MilitanteInputDTO {

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    private String apellido2;

    @NotNull(message = "La organización es obligatoria")
    private Organizacion organizacion;

    private DireccionDTO direccion;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    private String email;

    private Boolean estudiante;

    @Past(message = "La fecha de nacimiento debe estar en el pasado")
    private LocalDate fechaNacimiento;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String numeroCarnet;

    @NotNull(message = "El sexo es obligatorio")
    private Sexo sexo;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^\\+?(\\d{1,3})?[-.\\s]?\\(?(\\d{1,4})\\)?[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,9}$", message = "El teléfono debe ser válido")
    private String telefono;

    private Boolean trabajador;

    private Integer comiteBaseId;

    private Set<Rol> roles = new HashSet<>();

    private Boolean sindicado;

    private Boolean premilitante;

    private Boolean activar; // Ojo, no creará el usuario como activo, sino que si es true se le enviará el email de activación

    private Idioma idioma;

    private Integer cuota;

    // Campos añadidos desde el formulario de Angular
    private Integer actividadEconomica;
    private Integer tipoContrato;
    private Integer modalidadTrabajo;
    private ExisteOrganoRepresentacionTrabajadores existeOrganoRepresentacion;
    private Boolean participaOrganoRepresentacion;
    private String profesion;
    private String nombreEmpresa;
    private Integer numeroTrabajadores;
    private String nombreCentroTrabajo;
    private Integer numeroTrabajadoresCentroTrabajo;
    private String direccionCentroTrabajo;
    private LocalDate fechaInicioContrato;

    // Campos añadidos desde el formulario de Angular (estudiante)
    private Integer nivelEstudio;
    private Integer tipoEstudio;
    private Integer subtipoEstudio;
    private String nombreCentroEducativo;
    private String nombreEstudios;
    private Integer anhoFinalizacion;
    private Boolean sindicatoEstudiantil;

    // Campos añadidos para sindicacion
    private Integer sindicato;
    private Integer federacion;
    private String cargo;
    private Boolean participaAreaJuventud;
    private String sindicatoOtros;
    private String federacionOtros;

    @AssertTrue(message = "El número de carnet es obligatorio si premilitante es false")
    public boolean isNumeroCarnetValid() {
        return Boolean.TRUE.equals(premilitante) || (numeroCarnet != null && !numeroCarnet.trim().isEmpty() && numeroCarnet.trim().length()==4);
    }

    @AssertTrue(message = "La cuota es obligatoria si premilitante es false y debe ser mayor que 5")
    public boolean isCuotaValid() {
        if (Boolean.TRUE.equals(premilitante)) {
            return cuota == null || cuota == 0;
        } else {
            return cuota != null && cuota > 5;
        }
    }
}
