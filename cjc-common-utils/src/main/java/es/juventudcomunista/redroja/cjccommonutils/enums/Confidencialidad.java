package es.juventudcomunista.redroja.cjccommonutils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Grados de confidencialidad de un documento.
 */
@Getter
@AllArgsConstructor
public enum Confidencialidad {
    PUBLICO("Público"),
    INTERNO("Interno"),
    RESTRINGIDO("Restringido"),
    CONFIDENCIAL("Confidencial");

    private final String descripcion;
}