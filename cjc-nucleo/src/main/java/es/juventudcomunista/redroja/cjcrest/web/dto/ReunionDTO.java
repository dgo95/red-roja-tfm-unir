package es.juventudcomunista.redroja.cjcrest.web.dto;

import es.juventudcomunista.redroja.cjcrest.entity.Invitado;
import lombok.Data;

import jakarta.validation.Valid;

import java.time.Instant;
import java.util.List;

@Data
public class ReunionDTO {
    private String direccion;
    private Integer duracion;
    private Instant fecha;
    private List<Invitado> invitados;
    private boolean premilitantes;
    @Valid
    private List<PuntoDTO> puntos;
}