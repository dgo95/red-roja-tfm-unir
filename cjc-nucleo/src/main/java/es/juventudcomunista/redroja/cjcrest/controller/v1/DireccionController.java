package es.juventudcomunista.redroja.cjcrest.controller.v1;

import es.juventudcomunista.redroja.cjcrest.service.DireccionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import es.juventudcomunista.redroja.cjccommonutils.web.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/direccion")
@RequiredArgsConstructor
@Slf4j
public class DireccionController {

    private final DireccionService direccionService;

    @GetMapping("/{idComunidad}/getProvincias")
    public ResponseEntity<ApiResponse> getProvincias(@PathVariable int idComunidad) {
        var provincias = direccionService.obtenerProvinciasPorComunidadId(idComunidad);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.success("Provincias de la comunidad cargadas", provincias);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{idProvincia}/getMunicipios")
    public ResponseEntity<ApiResponse> getMunicipios(@PathVariable int idProvincia) {
        var municipios = direccionService.obtenerMunicipiosPorProvinciaId(idProvincia);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.success("Municipios de la provincia cargadas", municipios);
        return ResponseEntity.ok(apiResponse);
    }
}
