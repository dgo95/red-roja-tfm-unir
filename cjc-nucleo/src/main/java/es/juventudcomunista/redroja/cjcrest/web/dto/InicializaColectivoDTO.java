package es.juventudcomunista.redroja.cjcrest.web.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class InicializaColectivoDTO {
    private List<ResponsabilidadDTO> responsabilidades;
    private List<ResponsableDTO> militantes;
    private Map<String,Object> reunion;
    private String sede;
    private String nombre;
}
