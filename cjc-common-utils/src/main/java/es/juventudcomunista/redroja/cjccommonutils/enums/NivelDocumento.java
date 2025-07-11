package es.juventudcomunista.redroja.cjccommonutils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Nivel organizativo al que pertenece el documento.
 */
@Getter
@AllArgsConstructor
public enum NivelDocumento {
    COLECTIVO("Colectivo de base"),
    INTERMEDIO("Comité intermedio"),
    CENTRAL("Comité central");
    private final String descripcion;
}