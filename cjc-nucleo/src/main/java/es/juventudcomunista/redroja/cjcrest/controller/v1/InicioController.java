package es.juventudcomunista.redroja.cjcrest.controller.v1;

import es.juventudcomunista.redroja.cjcrest.service.MilitanteService;
import es.juventudcomunista.redroja.cjcrest.web.dto.MilitanteDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import es.juventudcomunista.redroja.cjccommonutils.web.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/inicio")
@Slf4j
@RequiredArgsConstructor
public class InicioController {


    private final MilitanteService militanteService;

    @GetMapping
    public ResponseEntity<ApiResponse> getdatosBasicos() {
        String id;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            ApiResponse api = new ApiResponse();
            api.error(401, "", "Usuario no autenticado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(api);
        }
        id = authentication.getName(); // Suponiendo que el nombre del usuario autenticado es el id
        log.debug("getdatosBasicos - Se obtienen los datos del militante con id: {}", id);
        MilitanteDTO datos = militanteService.obtieneDatosBasicos(id);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.success("Datos básicos proporcionados", datos);
        log.info("Formulario de perfil inicializado exitosamente para el militante con carnet: {}", id);
        return ResponseEntity.ok(apiResponse);
    }
}
