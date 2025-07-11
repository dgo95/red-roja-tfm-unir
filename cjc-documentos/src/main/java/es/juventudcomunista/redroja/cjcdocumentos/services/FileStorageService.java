package es.juventudcomunista.redroja.cjcdocumentos.services;

import es.juventudcomunista.redroja.cjcdocumentos.storage.CryptoFileInfo;
import es.juventudcomunista.redroja.cjcdocumentos.storage.DecryptedFile;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Responsabilidad ÚNICA: leer, escribir y borrar ficheros binarios.
 * La capa de dominio solo conoce esta interfaz; no sabe si detrás
 * hay disco local, S3, MinIO, Azure Blob, etc.
 */
public interface FileStorageService {

    /**
     * Guarda el flujo en la ruta relativa indicada.
     * @param in                contenido del fichero
     * @param relativePath      ej. "2025/05/20/0e89d9ef-c.docx"
     * @return información útil para persistir en la BD
     */
    CryptoFileInfo store(InputStream in, String relativePath) throws IOException;

    DecryptedFile load(String relativePath, CryptoFileInfo meta) throws IOException;


    /**
     * Devuelve el recurso legible (se usa en descargas, thumbnails, …).
     */
    Resource loadAsResource(String relativePath) throws IOException;

    /**
     * Elimina el objeto físico.  Si ya no existe se considera éxito.
     */
    void delete(String relativePath) throws IOException;
}
