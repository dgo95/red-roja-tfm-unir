package es.juventudcomunista.redroja.cjccommonutils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Ámbitos o frentes de trabajo a los que se asocia el documento.
 */
@Getter
@AllArgsConstructor
public enum Categoria {
    OBRERO("Movimiento obrero"),
    ESTUDIANTIL("Movimiento estudiantil"),
    POLITICO("Político"),
    ORGANIZACION("Organización"),
    AGITACION("Agitación y propaganda"),
    FORMACION("Formación"),
    MUJER("Mujer trabajadora"),
    VECINAL("Movimiento vecinal"),
    OTRO("Otro");

    private final String descripcion;
}