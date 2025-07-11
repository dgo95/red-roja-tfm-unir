package es.juventudcomunista.redroja.cjccommonutils.service;

import es.juventudcomunista.redroja.cjccommonutils.enums.TipoCenso;
import es.juventudcomunista.redroja.cjccommonutils.web.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "cjc-rest", url = "${cjc.rest}")
public interface RestClient {

    @GetMapping("/v1/colectivo/{id}/{tipo}/getCenso")
    ResponseEntity<ApiResponse> getCenso(@PathVariable int id, @PathVariable TipoCenso tipo,
                                         @RequestParam(required = false) Integer page,
                                         @RequestParam(required = false) Integer size);

    @GetMapping("/v1/militantes/{militanteId}/ficha-movilidad-datos")
    ResponseEntity<ApiResponse> obtenerDatosFichaMovilidad(@PathVariable String militanteId);
}

