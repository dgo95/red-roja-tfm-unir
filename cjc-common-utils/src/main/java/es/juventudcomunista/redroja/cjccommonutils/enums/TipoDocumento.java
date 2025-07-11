package es.juventudcomunista.redroja.cjccommonutils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Tipos o naturaleza de los documentos.
 */
@Getter
@AllArgsConstructor
public enum TipoDocumento {
    ACTA("Acta"),
    ESTATUTO("Estatuto"),
    COMUNICADO("Comunicado"),
    FORMATIVO("Formativo"),
    TESIS("Tesis"),
    PLAN("Plan de trabajo"),
    RESOLUCION("Resolución"),
    INFORME("Informe"),
    CIRCULAR("Circular"),
    CARTA("Carta"),
    PROPAGANDA("Propaganda"),
    OTRO("Otro");

    private final String descripcion;
}