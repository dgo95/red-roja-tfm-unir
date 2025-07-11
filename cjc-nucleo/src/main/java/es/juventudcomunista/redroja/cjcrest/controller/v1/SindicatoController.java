package es.juventudcomunista.redroja.cjcrest.controller.v1;

import es.juventudcomunista.redroja.cjcrest.service.SindicatoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import es.juventudcomunista.redroja.cjccommonutils.web.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/sindicato")
@RequiredArgsConstructor
@Slf4j
public class SindicatoController {

    private final SindicatoService sindicatoService;

    @GetMapping("/{idSindicato}/getFederaciones")
    public ResponseEntity<ApiResponse> getFederaciones(@PathVariable int idSindicato) {
        var federaciones = sindicatoService.getAllFederacionesBySindicato(idSindicato);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.success("Federaciones del sindicato cargadas", federaciones);
        return ResponseEntity.ok(apiResponse);
    }
}