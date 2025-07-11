package es.juventudcomunista.redroja.cjcdocumentos.dto;

import org.springframework.core.io.Resource;

/**
 * Contiene todo lo necesario para construir la respuesta HTTP del fichero.
 */
public record FicheroDescarga(Resource recurso,
                              String nombreOriginal,
                              String mimeType) { }
