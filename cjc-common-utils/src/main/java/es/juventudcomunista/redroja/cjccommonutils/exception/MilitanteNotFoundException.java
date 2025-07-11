package es.juventudcomunista.redroja.cjccommonutils.exception;


public class MilitanteNotFoundException extends RuntimeException {
    public MilitanteNotFoundException(String message) {
        super(message);
    }

    public MilitanteNotFoundException(Long id) {
        super("Militante with ID " + id + " not found");
    }
}

