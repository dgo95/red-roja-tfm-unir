package es.juventudcomunista.redroja.cjcrest.controller.handler;

import es.juventudcomunista.redroja.cjcrest.exception.AnioPasadoException;
import es.juventudcomunista.redroja.cjcrest.exception.EstudioValidationException;
import es.juventudcomunista.redroja.cjcrest.exception.MilitanteNotFoundException;
import lombok.extern.slf4j.Slf4j;
import es.juventudcomunista.redroja.cjccommonutils.web.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Manejo de una excepción genérica Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> globalExceptionHandler(Exception ex) { // Si en el futuro se quiere personalizar aún más el mensaje se puede aumentar los parámetros recibidos con WebRequest request
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),  ex.getMessage(),"Ha ocurrido un error interno. Inténtelo de nuevo más tarde o avise a un responsable.");

        log.error("Ha ocurrido un error interno: {}",ex.getMessage(),ex);
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Manejo específico de la excepción MilitanteNotFoundException
    @ExceptionHandler(MilitanteNotFoundException.class)
    public ResponseEntity<ApiResponse> handleMilitanteNotFoundException(MilitanteNotFoundException ex) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.error(HttpStatus.BAD_REQUEST.value(), "El perfil solicitado no se ha encontrado.", ex.getMessage());
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    // Manejo de excepción para validación de datos de entrada
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.error(HttpStatus.BAD_REQUEST.value(), "Error de validación en los datos de entrada.", errors.toString());
        log.error("Error de validación en los datos de entrada: {}",ex.getMessage(),ex);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    // Manejo específico de la excepción AnioPasadoException
    @ExceptionHandler(AnioPasadoException.class)
    public ResponseEntity<ApiResponse> handleAnioPasadoException(AnioPasadoException ex) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.error(HttpStatus.BAD_REQUEST.value(), "El año de finalización es inválido.", ex.getMessage());
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    // Manejo específico de la excepción EstudioValidationException
    @ExceptionHandler(EstudioValidationException.class)
    public ResponseEntity<ApiResponse> handleEstudioValidationException(EstudioValidationException ex) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.error(HttpStatus.BAD_REQUEST.value(), "Datos del estudio inválidos.", ex.getMessage());
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleValidacionException(HttpMessageNotReadableException ex) {
        ApiResponse apiResponse = new ApiResponse();
        log.error("El JSON está malformado: {}",ex.getMessage());
        apiResponse.error(HttpStatus.BAD_REQUEST.value(), "JSON mal formado.", ex.getMessage());
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    // Aquí se pueden añadir más métodos de excepciones específicas según sea necesario
}


