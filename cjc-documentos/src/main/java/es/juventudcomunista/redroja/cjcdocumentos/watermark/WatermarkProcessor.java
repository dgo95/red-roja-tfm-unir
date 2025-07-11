package es.juventudcomunista.redroja.cjcdocumentos.watermark;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Cada implementación sabe incrustar o “sanear” su tipo de fichero.
 * El contenido se escribe en {@code out}; la llamada cierra ambos streams.
 */
@FunctionalInterface
public interface WatermarkProcessor {

    /**
     * @param in  flujo PDF ya desencriptado
     * @param out dónde escribir la versión marcada
     * @param info datos (uid, timestamp…)
     */
    void apply(InputStream in, OutputStream out, WatermarkInfo info) throws Exception;

    /** Mime-types que sabe manejar esta implementación.  */
    default boolean supports(String mimeType) { return false; }
}
