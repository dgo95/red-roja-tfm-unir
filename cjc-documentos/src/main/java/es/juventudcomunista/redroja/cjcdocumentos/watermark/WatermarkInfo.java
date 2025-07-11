package es.juventudcomunista.redroja.cjcdocumentos.watermark;

import java.time.Instant;

/** Información mínima que todos los procesadores de marca de agua necesitan. */
public record WatermarkInfo(
        String documentId,
        String userId,
        Instant downloadedAtUtc
) {}
