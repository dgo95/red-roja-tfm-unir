package es.juventudcomunista.redroja.cjcdocumentos.controller;

import java.io.IOException;

import es.juventudcomunista.redroja.cjcdocumentos.services.AlmacenamientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/fotosPerfil")
@Slf4j
@RequiredArgsConstructor
public class FotoPerfilController {
	

    private final AlmacenamientoService almacenamientoService;

    @PostMapping("/{id}/actualizarImagenPerfil")
    public ResponseEntity<?> actualizarImagenPerfil(@PathVariable Long id,
                                                    @RequestParam("imagen") MultipartFile imagen) throws IOException {
            String rutaImagen = almacenamientoService.guardarImagen(id, imagen);
            return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Resource> serveFile(@PathVariable String id, @RequestParam String sexo) {
    	Integer s = 0;
    	if("MASCULINO".equals(sexo)) {
    		s = 1;
    	}
        Resource resource = almacenamientoService.obtenerFotoPerfil(id,s);

        // Asegúrate de que el Content-Type sea "image/jpg"
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}