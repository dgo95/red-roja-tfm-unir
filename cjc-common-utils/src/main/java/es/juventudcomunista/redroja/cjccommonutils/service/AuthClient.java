package es.juventudcomunista.redroja.cjccommonutils.service;

import es.juventudcomunista.redroja.cjccommonutils.web.ApiResponse;
import es.juventudcomunista.redroja.cjccommonutils.dto.UsuarioAuthInputDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
@FeignClient(name = "cjc-auth", url = "${cjc.auth}")
public interface AuthClient {

    @PostMapping("/v1")
    ResponseEntity<ApiResponse> create(@RequestBody UsuarioAuthInputDTO request);

    @PutMapping("/v1/{militanteId}/promocion")
    ResponseEntity<ApiResponse> promocion(@PathVariable("militanteId") String militanteId,@RequestBody UsuarioAuthInputDTO request);

    @DeleteMapping("/v1/{militanteId}")
    ResponseEntity<ApiResponse> deleteUsuario(@PathVariable("militanteId") String militanteId);

    @PostMapping("/v1/creaCodigo/{militanteId}/{codigo}")
    ResponseEntity<ApiResponse> creaCodigo(@PathVariable("militanteId") String militanteId,@PathVariable("codigo") String codigo);
} **/
