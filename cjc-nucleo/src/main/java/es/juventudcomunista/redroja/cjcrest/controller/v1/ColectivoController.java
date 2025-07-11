package es.juventudcomunista.redroja.cjcrest.controller.v1;

import es.juventudcomunista.redroja.cjcrest.dto.*;
import es.juventudcomunista.redroja.cjcrest.entity.MaterialInventario;
import es.juventudcomunista.redroja.cjccommonutils.enums.TipoCenso;
import es.juventudcomunista.redroja.cjcrest.exception.MilitanteNotFoundException;
import es.juventudcomunista.redroja.cjcrest.service.ColectivoService;
import es.juventudcomunista.redroja.cjcrest.web.dto.InicializaColectivoDTO;
import es.juventudcomunista.redroja.cjcrest.web.dto.ResponsabilidadDTO;
import es.juventudcomunista.redroja.cjcrest.web.dto.ReunionDTO;
import es.juventudcomunista.redroja.cjcrest.web.input.ColectivoInputDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import es.juventudcomunista.redroja.cjccommonutils.web.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/colectivo")
@Slf4j
@RequiredArgsConstructor
public class ColectivoController {


    private final ColectivoService colectivoService;

    @PostMapping
    public ResponseEntity<ApiResponse> crearColectivo(@RequestBody @Valid ColectivoInputDTO colectivoDTO) {

        var response = new ApiResponse();

        var colectivo = colectivoService.crear(colectivoDTO);

        response.success("Colectivo creado correctamente", colectivo);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> obtenerColectivoPorId(@PathVariable Integer id) {
        var response = new ApiResponse();
        var colectivo = colectivoService.obtenerDtoPorId(id);
        response.success("Colectivo obtenido correctamente", colectivo);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse> obtenerTodosLosColectivos() {
        var response = new ApiResponse();
        List<ComiteBaseDTO> colectivos = colectivoService.obtenerTodos();
        response.success("Colectivos obtenidos correctamente", colectivos);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> actualizarColectivo(@PathVariable Integer id, @RequestBody @Valid ColectivoInputDTO colectivoDTO) {
        var response = new ApiResponse();
        var colectivo = colectivoService.actualizar(id, colectivoDTO);
        response.success("Colectivo actualizado correctamente", colectivo);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> eliminarColectivo(@PathVariable Integer id) {
        var response = new ApiResponse();

        // id del colectivo por defecto que no se puede borrar
        final int COLECTIVO_POR_DEFECTO_ID = 1;

        if (id == COLECTIVO_POR_DEFECTO_ID) {
            response.error(HttpStatus.FORBIDDEN.value(), "Este colectivo es necesario.", "El colectivo por defecto no se puede borrar.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        colectivoService.eliminar(id);
        response.success("Colectivo eliminado correctamente", null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/inicializaColectivo")
    @PreAuthorize("@authorizationService.isMemberOfCommittee(authentication, #id)")
    public ResponseEntity<ApiResponse> getInicializaFormulario(@PathVariable int id)
            throws MilitanteNotFoundException {
        log.debug("getInicializaFormulario - Se inicia para el colectivo con ID: {}", id);
        try {
            InicializaColectivoDTO formularioPerfil = colectivoService.inicializaColectivoDe(id);
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.success("Colectivo inicializado exitosamente", formularioPerfil);
            log.info("Formulario de perfil inicializado exitosamente para el militante con ID: {}", id);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (MilitanteNotFoundException e) {
            log.error("Militante con ID: {} no encontrado", id);
            throw e;
        }
    }

    @PutMapping("{comiteBaseId}/cambiarMilitanteDeComiteBase/{militanteId}/")
    @PreAuthorize(
            "@authorizationService.hasAnyRoleForCollective(authentication, #id, Rol.POLITICO, Rol.ORGANIZACION)"
    )
    public ResponseEntity<ApiResponse> cambiarDeComiteBase(@PathVariable String militanteId, @PathVariable Integer comiteBaseId) {
        try {
            colectivoService.cambiarDeComiteBaseAMilitante(comiteBaseId, militanteId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (MilitanteNotFoundException | EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{idColectivo}/actualizarResponsabilidades")
    @PreAuthorize(
            "@authorizationService.hasAnyRoleForCollective(authentication, #idColectivo, T(es.juventudcomunista.redroja.cjccommonutils.enums.Rol).DIR_POLITICA, T(es.juventudcomunista.redroja.cjccommonutils.enums.Rol).ORGANIZACION)"
    )
    public ResponseEntity<ApiResponse> actualizarResponsabilidades(@PathVariable int idColectivo,
                                                                   @RequestBody List<ResponsabilidadDTO> responsabilidades) throws MilitanteNotFoundException {
        log.debug("actualizarResponsabilidades - Se inicia para el colectivo con ID: {}", idColectivo);
        try {
            colectivoService.actualizarResponsabilidadesDe(idColectivo, responsabilidades);
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.success("Responsabilidades actualizadas exitosamente",
                    "Las responsabilidades del militante han sido actualizadas");
            log.info("Responsabilidades actualizadas exitosamente para el militante con ID: {}", idColectivo);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (MilitanteNotFoundException e) {
            log.error("Militante con ID: {} no encontrado", idColectivo);
            throw e;
        }
    }

    @PutMapping("/{id}/reunion")
    @PreAuthorize(
            "@authorizationService.hasAnyRoleForCollective(authentication, #id, Rol.POLITICO, Rol.ORGANIZACION)"
    )
    public ResponseEntity<ApiResponse> actualizarReunion(@PathVariable int id,
                                                         @RequestBody ReunionDTO reunionDTO) throws MilitanteNotFoundException {

        var i = colectivoService.editaReunion(id, reunionDTO);

        return getInicializaFormulario(i);
    }

    @PreAuthorize(
            "@authorizationService.hasAnyRoleForCollective(authentication, #id, Rol.POLITICO, Rol.ORGANIZACION)"
    )
    @PostMapping("/{id}/reunion")
    public ResponseEntity<ApiResponse> crearReunion(@PathVariable Integer id,
                                                    @RequestBody ReunionDTO reunionDTO) throws MilitanteNotFoundException {

        colectivoService.convocaReunion(id, reunionDTO);

        return getInicializaFormulario(id);
    }


    @GetMapping("/{id}/{tipo}/getCenso")
    public ResponseEntity<ApiResponse> getCenso(@PathVariable int id, @PathVariable TipoCenso tipo, Pageable pageable) {
        switch (tipo) {
            case GENERAL:
                return getCensoGeneral(id, pageable);
            case MOS:
                return getCensoLaboral(id, pageable);
            case MES:
                return getCensoEstudiantil(id, pageable);
            default:
                ApiResponse apiResponse = new ApiResponse();
                apiResponse.error(400, "No existe el censo", "");
                return ResponseEntity.badRequest().body(apiResponse);
        }
    }

    @GetMapping("/{id}/censoGeneral")
    public ResponseEntity<ApiResponse> getCensoGeneral(@PathVariable int id, Pageable pageable) {
        log.debug("getCensoGeneral - Se inicia para el Colectivo con ID: {}", id);
        var censo = colectivoService.censoGeneral(id, pageable);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.success("Censo general inicializado exitosamente", censo);
        log.info("Formulario de perfil inicializado exitosamente para el militante con ID: {}", id);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/{id}/censoLaboral")
    public ResponseEntity<ApiResponse> getCensoLaboral(@PathVariable int id, Pageable pageable) {
        log.debug("getCensoLaboral - Se inicia para el Colectivo con ID: {}", id);
        var censo = colectivoService.censoLaboral(id, pageable);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.success("Censo general inicializado exitosamente", censo);
        log.info("Formulario de perfil inicializado exitosamente para el colectivo con ID: {}", id);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/{id}/censoEstudiantil")
    public ResponseEntity<ApiResponse> getCensoEstudiantil(@PathVariable int id, Pageable pageable) {
        log.debug("getCensoEstudiantil - Se inicia para el Colectivo con ID: {}", id);
        Page<CensoEstudiantilDTO> censo = colectivoService.censoEstudiantil(id, pageable);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.success("Censo estudiantil inicializado exitosamente", censo);
        log.info("Formulario de perfil inicializado exitosamente para el colectivo con ID: {}", id);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/{id}/inventario")
    public ResponseEntity<ApiResponse> getInventario(@PathVariable int id, Pageable pageable) {
        log.debug("getInventario - Se inicia para el Colectivo con ID: {}", id);
        Page<MaterialDTO> inventario = colectivoService.getInventario(id, pageable);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.success("Inventario inicializado exitosamente", inventario);
        log.info("Inventario inicializado exitosamente para el colectivo con ID: {}", id);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/{id}/material")
    public ResponseEntity<ApiResponse> addNewMaterial(@PathVariable int id, @RequestBody MaterialInputDTO materialCreateDTO) {
        log.debug("addNewMaterial - Se inicia para el Colectivo con ID: {}", id);
        MaterialInventario newMaterial = colectivoService.addNewMaterial(id, materialCreateDTO);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.success("Material añadido exitosamente", newMaterial);
        log.info("Material añadido exitosamente para el colectivo con ID: {}", id);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @GetMapping("/inicializaContacto")
    public ResponseEntity<ApiResponse> inicializaContacto(@RequestParam(required = false) Long contactoId,@RequestParam(required = false) Integer comiteId ) {
        ApiResponse apiResponse = new ApiResponse();
        log.debug("inicializaContacto - Se inicia para el contacto con ID: {}", contactoId);
        // Lógica para inicializar un contacto específico
        ContactoDTO contactoDTO = colectivoService.inicializaContacto(contactoId,comiteId);
        apiResponse.success("Contacto inicializado correctamente", contactoDTO);
        log.info("Contacto inicializado correctamente para el ID: {}", contactoId);
        return ResponseEntity.ok(apiResponse);
    }
}
