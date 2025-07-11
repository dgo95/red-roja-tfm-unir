package es.juventudcomunista.redroja.cjcdocumentos.execption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DocumentoNoEncontradoException extends RuntimeException {
    public DocumentoNoEncontradoException(String uuid) {
        super("No se encontró ningún documento con uuid = " + uuid);
    }
}

