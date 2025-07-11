package es.juventudcomunista.redroja.cjcdocumentos.dto;

import es.juventudcomunista.redroja.cjccommonutils.enums.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;

/**
 * DTO de entrada para actualización parcial de un documento
 * (PATCH). Todos los campos son opcionales salvo el título.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarDocumentoDTO {

    @NotBlank(message = "El título no puede ser vacío")
    private String titulo;
    @NotNull
    private NivelDocumento      nivel;
    @NotNull
    private Confidencialidad    confidencialidad;
    @NotNull
    private TipoDocumento       tipo;
    private Set<Categoria>      categorias;
}
