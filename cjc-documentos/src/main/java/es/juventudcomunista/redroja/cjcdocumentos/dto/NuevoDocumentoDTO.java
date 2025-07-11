package es.juventudcomunista.redroja.cjcdocumentos.dto;

import es.juventudcomunista.redroja.cjccommonutils.enums.Categoria;
import es.juventudcomunista.redroja.cjccommonutils.enums.Confidencialidad;
import es.juventudcomunista.redroja.cjccommonutils.enums.TipoDocumento;import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NuevoDocumentoDTO {
    @NotBlank private String titulo;
    @NotNull  private LocalDate fecha;
    @NotNull  private Confidencialidad confidencialidad;
    @NotNull  private TipoDocumento tipo;
    @NotNull  private Categoria categoria;
    @NotBlank  private String propietario;
    @NotNull  private MultipartFile archivo;
}