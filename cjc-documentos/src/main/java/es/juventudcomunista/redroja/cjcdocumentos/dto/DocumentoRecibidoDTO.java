package es.juventudcomunista.redroja.cjcdocumentos.dto;

import es.juventudcomunista.redroja.cjccommonutils.enums.Categoria;
import es.juventudcomunista.redroja.cjccommonutils.enums.Confidencialidad;
import es.juventudcomunista.redroja.cjccommonutils.enums.NivelDocumento;
import es.juventudcomunista.redroja.cjccommonutils.enums.TipoDocumento;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentoRecibidoDTO {
    private String id;
    private String titulo;
    private LocalDate fecha;
    private NivelDocumento nivel;
    private Confidencialidad confidencialidad;
    private TipoDocumento tipo;
    private Categoria categoria;
    private String propietario; // nombre descriptivo
}