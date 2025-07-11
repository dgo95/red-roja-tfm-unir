package es.juventudcomunista.redroja.cjcrest.controller.v1;

import es.juventudcomunista.redroja.cjcrest.dto.FichaMovilidadInputDTO;
import es.juventudcomunista.redroja.cjcrest.dto.PromocionarMilitanteInput;
import es.juventudcomunista.redroja.cjcrest.service.MilitanteService;
import es.juventudcomunista.redroja.cjcrest.web.input.MilitanteInputDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import es.juventudcomunista.redroja.cjccommonutils.web.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/militantes")
@Slf4j
@RequiredArgsConstructor
public class MilitanteController {


    private final MilitanteService militanteService;

    @PostMapping
    @PreAuthorize(
            "@authorizationService.hasAnyRoleForCollective(authentication, #militanteDTO.comiteBaseId, T(es.juventudcomunista.redroja.cjccommonutils.enums.Rol).DIR_POLITICA, T(es.juventudcomunista.redroja.cjccommonutils.enums.Rol).ORGANIZACION)"
    )
    public ResponseEntity<ApiResponse> crearMilitante(@RequestBody @Valid MilitanteInputDTO militanteDTO) {

        var response = new ApiResponse();

        var m = militanteService.crear(militanteDTO);

        response.success("Militante creado creado correctamente", m);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/activar")
    public ResponseEntity<ApiResponse> activarMilitante(@PathVariable String id) {
        var response = new ApiResponse();
        militanteService.activarMilitante(id);

        response.success("Militante activado correctamente.",null);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/conceder-militancia")
    public ResponseEntity<ApiResponse> activarMilitante(@PathVariable String id, @RequestBody @Valid PromocionarMilitanteInput dto) {
        var response = new ApiResponse();
        militanteService.promocionarMilitante(id,dto);

        response.success("Militante activado correctamente",null);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> editarMilitante(@RequestBody MilitanteInputDTO militanteDTO, @PathVariable String id) {
        var response = new ApiResponse();

        var m = militanteService.editar(id,militanteDTO);
        response.success("Militante editado correctamente", m);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> obtenerMilitantePorId(@PathVariable String id) {
        var response = new ApiResponse();
        var colectivo = militanteService.obtenerPorId(id);
        response.success("Militante obtenido correctamente", colectivo);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse> obtenerTodosLosMilitantes() {
        var response = new ApiResponse();
        var militantes = militanteService.obtenerTodos();
        response.success("Militantes obtenidos correctamente", militantes);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{comiteBaseId}/{id}")
    @PreAuthorize(
            "@authorizationService.hasAnyRoleForCollective(authentication, #comiteBaseId, T(es.juventudcomunista.redroja.cjccommonutils.enums.Rol).DIR_POLITICA, T(es.juventudcomunista.redroja.cjccommonutils.enums.Rol).ORGANIZACION)"
    )
    public ResponseEntity<ApiResponse> eliminarMilitante(@PathVariable String id,@PathVariable Long comiteBaseId) {
        var response = new ApiResponse();

        militanteService.eliminar(id);
        response.success("Militante eliminado correctamente", null);
        return ResponseEntity.ok(response);
    }

    /*Ficha movilidad*/
    @PostMapping("/{militanteId}/ficha-movilidad")
    public ResponseEntity<ApiResponse> crearFichaMovilidad(@PathVariable String militanteId, @RequestBody @Valid FichaMovilidadInputDTO fichaMovilidadDTO) {
        var response = new ApiResponse();
        var fichaMovilidad = militanteService.crearFichaMovilidad(militanteId, fichaMovilidadDTO);
        response.success("Ficha de movilidad creada correctamente", fichaMovilidad);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{militanteId}/ficha-movilidad")
    public ResponseEntity<ApiResponse> editarFichaMovilidad(@PathVariable String militanteId, @RequestBody @Valid FichaMovilidadInputDTO fichaMovilidadDTO) {
        var response = new ApiResponse();
        var fichaMovilidad = militanteService.editarFichaMovilidad(militanteId, fichaMovilidadDTO);
        response.success("Ficha de movilidad editada correctamente", fichaMovilidad);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{militanteId}/ficha-movilidad")
    public ResponseEntity<ApiResponse> obtenerFichaMovilidad(@PathVariable String militanteId) {
        var response = new ApiResponse();
        var fichaMovilidad = militanteService.obtenerFichaMovilidadPorMilitanteId(militanteId);
        response.success("Ficha de movilidad obtenida correctamente", fichaMovilidad);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{militanteId}/ficha-movilidad-datos")
    public ResponseEntity<ApiResponse> obtenerDatosFichaMovilidad(@PathVariable String militanteId) {
        var response = new ApiResponse();
        var fichaMovilidad = militanteService.obtenerFichaMovilidadDatosPorMilitanteId(militanteId);
        response.success("Ficha de movilidad obtenida correctamente", fichaMovilidad);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{militanteId}/ficha-movilidad")
    public ResponseEntity<ApiResponse> eliminarFichaMovilidad(@PathVariable String militanteId) {
        var response = new ApiResponse();
        militanteService.eliminarFichaMovilidad(militanteId);
        response.success("Ficha de movilidad eliminada correctamente", null);
        return ResponseEntity.ok(response);
    }


}
