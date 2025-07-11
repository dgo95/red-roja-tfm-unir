package es.juventudcomunista.redroja.cjccommonutils.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioAuthInputDTO {

    @NotBlank(message = "El ID del militante no puede estar vacío")
    @Size(max = 50, message = "El ID del militante no puede exceder los 50 caracteres")
    private String militanteId;

    private String numeroCarnet;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El email debe de ser válido")
    private String email;

    @NotNull(message = "El campo roles no puede estar vacío")
    private Set<MilitanteRolDTO> roles;

    private String codigoActivacion;
}
