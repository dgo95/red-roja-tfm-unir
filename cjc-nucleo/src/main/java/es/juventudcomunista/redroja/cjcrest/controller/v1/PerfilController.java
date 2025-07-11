package es.juventudcomunista.redroja.cjcrest.controller.v1;

import es.juventudcomunista.redroja.cjcrest.exception.AnioPasadoException;
import es.juventudcomunista.redroja.cjcrest.exception.EstudioValidationException;
import es.juventudcomunista.redroja.cjcrest.exception.MilitanteNotFoundException;
import es.juventudcomunista.redroja.cjcrest.service.MilitanteService;
import es.juventudcomunista.redroja.cjcrest.web.dto.DatosBasicosDTO;
import es.juventudcomunista.redroja.cjcrest.web.dto.DatosEstudioDTO;
import es.juventudcomunista.redroja.cjcrest.web.dto.DatosSindicacionDTO;
import es.juventudcomunista.redroja.cjcrest.web.dto.DatosTrabajoDTO;
import es.juventudcomunista.redroja.cjcrest.web.input.FormularioPerfilDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import es.juventudcomunista.redroja.cjccommonutils.web.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Calendar;

@RestController
@RequestMapping("/v1/perfil")
@RequiredArgsConstructor
@Slf4j
public class PerfilController {


    private final MilitanteService militanteService;

    @GetMapping("/{militanteId}/inicializaFormularioPerfil")
    public ResponseEntity<ApiResponse> getInicializaFormulario(@PathVariable String militanteId) throws MilitanteNotFoundException {
            FormularioPerfilDTO formularioPerfil = militanteService.inicializaFormularioPerfil(militanteId);
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.success("Formulario inicializado exitosamente", formularioPerfil);
            return  ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/inicializaFormularioPerfil")
    public ResponseEntity<ApiResponse> getInicializaFormulario(){
        FormularioPerfilDTO formularioPerfil = militanteService.inicializaFormularioPerfil();
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.success("Formulario inicializado exitosamente", formularioPerfil);
        return  ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}/basico")
    public ResponseEntity<ApiResponse> actualizarDatosBasicos(@PathVariable(name = "id") String id, @RequestBody @Valid DatosBasicosDTO datos) throws MilitanteNotFoundException {
        FormularioPerfilDTO formularioPerfil = militanteService.actualizaFormularioPerfil(id,datos);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.success("Formulario guardado exitosamente", formularioPerfil);
        return  ResponseEntity.ok(apiResponse);
    }
    
    @PutMapping("/{id}/estudio")
    public ResponseEntity<ApiResponse> actualizarDatosEstudio(@PathVariable String id, @RequestBody @Valid DatosEstudioDTO datos) throws Exception {
        if (datos.getAnhoFinalizacion() < Calendar.getInstance().get(Calendar.YEAR)) {
            throw new AnioPasadoException("El año de finalización debe ser mayor o igual al año actual.");
        }
        if (datos.getTipoEstudio() == 0 && datos.getSubtipoEstudio() != 0) {
            throw new EstudioValidationException("El año de finalización debe ser mayor o igual al año actual.");
        }
        FormularioPerfilDTO formularioPerfil = militanteService.actualizaFormularioPerfil(id,datos);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.success("Los datos del estudio se han guardado exitosamente", formularioPerfil);
        return  ResponseEntity.ok(apiResponse);
    }
    
    @PutMapping("/{id}/trabajo")
    public ResponseEntity<ApiResponse> actualizarDatosTrabajo(@PathVariable String id, @RequestBody @Valid DatosTrabajoDTO datos) {
        FormularioPerfilDTO formularioPerfil = militanteService.actualizaFormularioPerfil(id,datos);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.success("Los datos del estudio se han guardado exitosamente", formularioPerfil);
        return  ResponseEntity.ok(apiResponse);
    }
    
    @PutMapping("/{id}/sindicacion")
    public ResponseEntity<ApiResponse> actualizarDatosSindicacion(@PathVariable String id, @RequestBody @Valid DatosSindicacionDTO datos) {
        FormularioPerfilDTO formularioPerfil = militanteService.actualizaFormularioPerfil(id,datos);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.success("Los datos del estudio se han guardado exitosamente", formularioPerfil);
        return  ResponseEntity.ok(apiResponse);
    }
}


