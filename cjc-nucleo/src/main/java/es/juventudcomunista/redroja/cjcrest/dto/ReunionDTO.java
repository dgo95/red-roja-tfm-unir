package es.juventudcomunista.redroja.cjcrest.dto;

import es.juventudcomunista.redroja.cjcrest.web.dto.PuntoDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReunionDTO {
    private Integer id;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Integer duracion;
    private String direccion;
    private boolean terminada;
    private boolean aptaPremilitantes;
    private List<InvitadoDTO> invitados;
    private List<PuntoDTO> puntos;
}
