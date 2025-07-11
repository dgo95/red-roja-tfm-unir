package es.juventudcomunista.redroja.cjcrest.web.input;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import es.juventudcomunista.redroja.cjccommonutils.dto.MilitanteRolDTO;
import es.juventudcomunista.redroja.cjccommonutils.enums.Organizacion;

import java.util.List;
import java.util.Set;

@Data
public class ColectivoInputDTO {
    @NotNull(message = "Organización no puede ser nulo")
    private Organizacion organizacion;

    @NotBlank(message = "Nombre no puede estar vacío")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    private String nombre;

    @Size(max = 100, message = "La sede no puede tener más de 100 caracteres")
    private String sede;

    @NotBlank(message = "Email no puede estar vacío")
    @Email(message = "El email debe ser válido")
    private String email;

    // Para relaciones, puedes incluir IDs o listas de IDs si es necesario
    private Integer comiteDependienteId;

    private Set<MilitanteRolDTO> responsabilidadIds;

    @NotNull(message = "La lista de miembros no puede ser nula")
    @Size(min = 3, message = "Debe haber al menos tres militantes")
    private List<String> militantesIds;
}
