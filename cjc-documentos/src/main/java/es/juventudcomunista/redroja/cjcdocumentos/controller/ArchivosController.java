package es.juventudcomunista.redroja.cjcdocumentos.controller;

import es.juventudcomunista.redroja.cjccommonutils.enums.*;
import es.juventudcomunista.redroja.cjcdocumentos.dto.ActualizarDocumentoDTO;
import es.juventudcomunista.redroja.cjcdocumentos.dto.DocumentoRecibidoDTO;
import es.juventudcomunista.redroja.cjcdocumentos.dto.NuevoDocumentoDTO;
import es.juventudcomunista.redroja.cjcdocumentos.services.AlmacenamientoService;
import es.juventudcomunista.redroja.cjcdocumentos.services.impl.ExcelServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/archivos")
@Slf4j
@RequiredArgsConstructor
public class ArchivosController {

    private final ExcelServiceImpl excelServiceImpl;
    private final AlmacenamientoService almacenamientoService;

    @GetMapping("/xlsx/colectivo/censo/{tipo}/{id}")
    public void exportToExcel(@PathVariable int id, @PathVariable TipoCenso tipo, HttpServletResponse response)
            throws IOException {
        log.info("Inicio de exportación de datos al Excel");
        excelServiceImpl.exportToExcel(tipo,id,response);
        log.info("Exportación de Excel completada y respuesta HTTP configurada");
    }
    @GetMapping("/{militanteId}/ficha-movilidad/docx")
    public void descargarFichaMovilidadDocx(@PathVariable String militanteId, HttpServletResponse response)
            throws IOException {
        log.info("Inicio de descarga del archivo DOCX para la ficha de movilidad con ID: {}", militanteId);

        // Configura la respuesta HTTP para un archivo DOCX
        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        response.setHeader("Content-Disposition", "attachment; filename=ficha_movilidad_" + militanteId + ".docx");

        // Llama al servicio que genera el archivo DOCX
        almacenamientoService.generarFichaMovilidadDocx(militanteId, response.getOutputStream());

        log.info("Descarga del archivo DOCX completada y respuesta HTTP configurada");
    }


    @GetMapping("/{militanteId}/ficha-movilidad/pdf")
    public void descargarFichaMovilidadPdf(@PathVariable String militanteId, HttpServletResponse response) throws IOException {
        log.info("Inicio de descarga del archivo PDF para la ficha de movilidad con ID: {}", militanteId);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=ficha_movilidad_" + militanteId + ".pdf");

        try (ByteArrayOutputStream docxOutputStream = new ByteArrayOutputStream()) {
            // Generar el archivo DOCX en memoria
            almacenamientoService.generarFichaMovilidadDocx(militanteId, docxOutputStream);

            try (ByteArrayInputStream docxInputStream = new ByteArrayInputStream(docxOutputStream.toByteArray())) {
                // Convertir DOCX a PDF y escribirlo en el response output stream
                almacenamientoService.convertirDocxAPdf(docxInputStream, response.getOutputStream());
            }
        }
        log.info("Descarga del archivo PDF completada y respuesta HTTP configurada");
    }

    /**
     *
     * Devuelve un JSON con estructura: { data: DocumentoRecibidoDTO[], totalRecords: number }
     */
    @GetMapping
    @PreAuthorize(
            "@authorizationService.isMemberOfAll(authentication, #ids)"
    )
    public ResponseEntity<Map<String, Object>> listarDocumentos(
            @RequestParam List<String> ids,
            @RequestParam int page,
            @RequestParam int rows,
            @RequestParam(required = false) NivelDocumento filtroNivel,
            @RequestParam(required = false) Confidencialidad filtroConfidencialidad,
            @RequestParam(required = false) List<TipoDocumento> filtroTipo,
            @RequestParam(required = false) List<Categoria> filtroCategoria,
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false, defaultValue = "1") int sortOrder) {
        

        var resultado = almacenamientoService.buscar(ids, page, rows,
                filtroNivel, filtroConfidencialidad,
                filtroTipo, filtroCategoria,
                searchText, sortField, sortOrder);

        Map<String, Object> body = new HashMap<>();
        body.put("data", resultado.getContent());
        body.put("totalRecords", resultado.getTotalElements());

        return ResponseEntity.ok(body);
    }

    /**
     *
     *
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> subirDocumento(@ModelAttribute NuevoDocumentoDTO dto) {
        almacenamientoService.subir(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /* ─────────────────────────── GET /{uuid} ───────────────────────────── */

    @GetMapping("/{uuid}")
    public ResponseEntity<Resource> descargarDocumento(@PathVariable String uuid) {
        log.info("Petición de descarga de documento {}", uuid);

        var descarga = almacenamientoService.obtenerFicheroPorUuid(uuid);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(descarga.mimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + descarga.nombreOriginal() + "\"")
                .body(descarga.recurso());
    }

    /* ─────────────────────────── PATCH /{uuid} ─────────────────────────── */

    @PutMapping(value = "/{uuid}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DocumentoRecibidoDTO> editarDocumento(
            @PathVariable String uuid,
            @RequestBody @Valid ActualizarDocumentoDTO dto) {

        var actualizado = almacenamientoService.actualizar(uuid, dto);
        return ResponseEntity.ok(actualizado);
    }

    /* ─────────────────────────── DELETE /{uuid} ────────────────────────── */

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> borrarDocumento(@PathVariable String uuid, Authentication auth) {
        almacenamientoService.eliminar(uuid);
        return ResponseEntity.noContent().build();
    }
}
