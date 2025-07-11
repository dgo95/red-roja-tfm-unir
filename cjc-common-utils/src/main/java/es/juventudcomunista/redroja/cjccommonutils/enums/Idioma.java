package es.juventudcomunista.redroja.cjccommonutils.enums;


import lombok.Getter;

@Getter
public enum Idioma {
    CASTELLANO("es"),
    CATALA("ca"),
    EUSKERA("eu"),
    GALEGO("gl");

    private final String codigo;

    Idioma(String codigo) {
        this.codigo = codigo;
    }

    public static Idioma fromCodigo(String codigo) {
        for (Idioma idioma : Idioma.values()) {
            if (idioma.getCodigo().equalsIgnoreCase(codigo)) {
                return idioma;
            }
        }
        throw new IllegalArgumentException("Código de idioma no soportado: " + codigo);
    }
}

