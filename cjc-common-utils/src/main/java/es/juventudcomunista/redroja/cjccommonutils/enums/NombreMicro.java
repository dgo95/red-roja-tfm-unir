package es.juventudcomunista.redroja.cjccommonutils.enums;

import lombok.Getter;

@Getter
public enum NombreMicro {
    GATEWAY("cjc-gateway"),
    AUTH("cjc-auth"),
    REST("cjc-rest"),
    DOCUMENTOS("cjc-documentos"),
    EMAIL("cjc-email");

    private String nombre;

    NombreMicro(String nombre) {
        this.nombre = nombre;
    }

    public static NombreMicro fromNombre(String nombre) {
        for (NombreMicro nombreMicro : values()) {
            if (nombreMicro.getNombre().equalsIgnoreCase(nombre)) {
                return nombreMicro;
            }
        }
        throw new IllegalArgumentException("No enum constant with nombre: " + nombre);
    }
}
