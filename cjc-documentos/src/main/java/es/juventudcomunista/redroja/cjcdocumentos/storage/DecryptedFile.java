package es.juventudcomunista.redroja.cjcdocumentos.storage;

import java.io.InputStream;

public record DecryptedFile(
        InputStream data,
        long        sizePlain,
        String      mimeType
) {}

