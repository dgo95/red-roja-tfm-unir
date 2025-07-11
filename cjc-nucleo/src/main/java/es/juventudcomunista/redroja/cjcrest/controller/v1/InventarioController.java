package es.juventudcomunista.redroja.cjcrest.controller.v1;

import es.juventudcomunista.redroja.cjcrest.dto.CantidadMaterialDTO;
import es.juventudcomunista.redroja.cjcrest.dto.DatosMaterialDTO;
import es.juventudcomunista.redroja.cjcrest.dto.InventarioHistorialDTO;
import es.juventudcomunista.redroja.cjcrest.enums.ChangeType;
import es.juventudcomunista.redroja.cjcrest.service.InventarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import es.juventudcomunista.redroja.cjccommonutils.web.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/inventario")
@Slf4j
@RequiredArgsConstructor
public class InventarioController {


    private final InventarioService inventarioService;

    @PutMapping("/asignacion/{asignacionId}")
    public ResponseEntity<ApiResponse> updateMaterialCantidad(@PathVariable int asignacionId, @RequestBody CantidadMaterialDTO cantidad) {
        log.debug("updateMaterialCantidad - Se inicia para la Asignación de Material con ID: {}", asignacionId);
        inventarioService.updateMaterialCantidad(asignacionId, cantidad);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.success("Cantidad de material actualizada exitosamente", null);
        log.info("Cantidad de material actualizada exitosamente para la Asignación de Material con ID: {}", asignacionId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
    @PutMapping("/material/{materialId}")
    public ResponseEntity<ApiResponse> updateMaterialCantidad(@PathVariable int materialId, @RequestBody DatosMaterialDTO datos) {
        log.debug("updateMaterialCantidad - Se inicia para la Asignación de Material con ID: {}", materialId);
        inventarioService.updateMaterialDatos(materialId, datos);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.success("Cantidad de material actualizada exitosamente", null);
        log.info("los datos del material se han actualizado exitosamente para el Material con ID: {}", materialId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/{inventarioId}/historial")
    public ResponseEntity<ApiResponse> getHistorialInventario(@PathVariable int inventarioId, Pageable page,
                                                              @RequestParam(required = false) List<ChangeType> changeType,
                                                              @RequestParam(required = false) List<String> materialInventarioNombre) {
        log.debug("getHistorialInventario - Se inicia la obtención del inventario");
        Page<InventarioHistorialDTO> data = inventarioService.getHistorial(inventarioId, page, changeType, materialInventarioNombre);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.success("Historial cargado exitosamente", data);
        log.info("los datos del historial se han obtenido exitosamente: {}", inventarioId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
