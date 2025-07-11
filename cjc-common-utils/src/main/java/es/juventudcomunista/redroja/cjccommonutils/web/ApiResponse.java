package es.juventudcomunista.redroja.cjccommonutils.web;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ApiResponse {
    private Boolean success;
    private String message;
    private Object data;
    private Integer errorCode; // Campo para código de error
    private String errorDescription; // Descripción detallada del error
    private LocalDateTime timestamp;

    // Método para configurar una respuesta exitosa
    public void success(String message, Object data) {
        this.message = message;
        this.data = data;
        this.success = Boolean.TRUE;
        this.timestamp = LocalDateTime.now();
        // Asegurarse de que los campos de error estén limpios
        this.errorCode = null;
        this.errorDescription = null;
    }

    // Método para configurar una respuesta de error
    public void error(Integer errorCode, String errorDescription, String message) {
        this.success = Boolean.FALSE;
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
        this.timestamp = LocalDateTime.now();
        this.message = null;
        this.data = null;
    }
}

