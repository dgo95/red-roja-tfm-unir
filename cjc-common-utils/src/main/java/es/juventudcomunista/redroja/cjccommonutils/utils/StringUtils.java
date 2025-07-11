package es.juventudcomunista.redroja.cjccommonutils.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.text.Normalizer;
import java.util.regex.Pattern;

@Slf4j
@UtilityClass
public class StringUtils {

    private static final Pattern NON_ASCII_PATTERN = Pattern.compile("[^\\p{ASCII}]");

    public boolean esNulaOVacia(String str) {
        return str == null || str.isEmpty();
    }

    public boolean esBlanco(String str) {
        return str == null || str.trim().isEmpty();
    }

    public String capitalizar(String str) {
        if (esNulaOVacia(str)) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    public String eliminarEspacios(String str) {
        if (str == null) {
            return null;
        }
        return str.trim();
    }

    public String unir(String delimitador, String... elementos) {
        return String.join(delimitador, elementos);
    }

    public String invertir(String str) {
        if (esNulaOVacia(str)) {
            return str;
        }
        return new StringBuilder(str).reverse().toString();
    }

    public String eliminarAcentos(String str) {
        if (str == null) {
            return null;
        }
        String normalized = Normalizer.normalize(str, Normalizer.Form.NFD);
        return NON_ASCII_PATTERN.matcher(normalized).replaceAll("");
    }

    public String repetir(String str, int veces) {
        if (str == null || veces <= 0) {
            return "";
        }
        return str.repeat(veces);
    }

    public boolean contieneSoloDigitos(String str) {
        if (esNulaOVacia(str)) {
            return false;
        }
        return str.chars().allMatch(Character::isDigit);
    }

    public String[] dividir(String str, String delimitador) {
        if (str == null) {
            return new String[0];
        }
        return str.split(Pattern.quote(delimitador));
    }
}
