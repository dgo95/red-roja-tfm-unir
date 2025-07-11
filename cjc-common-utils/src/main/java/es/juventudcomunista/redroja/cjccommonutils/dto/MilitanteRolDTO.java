package es.juventudcomunista.redroja.cjccommonutils.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import es.juventudcomunista.redroja.cjccommonutils.enums.Rol;

@Data
@Builder
public class MilitanteRolDTO {
    @NotBlank
    private String militanteId;

    private Integer comite;
    private Integer comiteBase;

    @NotNull
    private Rol rol;
}
