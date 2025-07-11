package es.juventudcomunista.redroja.cjcdocumentos.crypto;

/** Envuelve/desenvuelve claves de datos simétricas. */
public interface KeyManagementService {
    /** Recibe una DEK sin envolver y la devuelve cifrada (KEK la define la implementación). */
    byte[] wrap(byte[] dek);

    /** Devuelve la DEK original a partir del blob envuelto. */
    byte[] unwrap(byte[] wrappedDek);
}

