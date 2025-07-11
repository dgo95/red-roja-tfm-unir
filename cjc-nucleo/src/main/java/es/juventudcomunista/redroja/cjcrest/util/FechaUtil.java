package es.juventudcomunista.redroja.cjcrest.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FechaUtil {

    public static String formatearFecha(LocalDateTime fecha, String formato) {
        if (fecha == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formato);
        return fecha.format(formatter);
    }
}

