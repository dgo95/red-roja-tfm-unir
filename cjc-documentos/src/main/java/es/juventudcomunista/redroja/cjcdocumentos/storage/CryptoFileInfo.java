package es.juventudcomunista.redroja.cjcdocumentos.storage;

public record CryptoFileInfo(
        String sha256Plain,          // hash del contenido original
        long   sizePlain,
        String ivB64,                // IV usado en AES-GCM (Base64)
        String dekWrappedB64         // DEK envuelta (Base64)
) {}

