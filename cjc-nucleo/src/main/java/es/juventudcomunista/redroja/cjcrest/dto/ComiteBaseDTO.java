package es.juventudcomunista.redroja.cjcrest.dto;

import lombok.Builder;
import lombok.Data;
import es.juventudcomunista.redroja.cjccommonutils.enums.Organizacion;

import java.util.Set;
import java.util.List;

@Data
@Builder
public class ComiteBaseDTO {
    private Integer id;

    private Organizacion organizacion;
    private String nombre;

    private String sede;

    private String email;

    private String nivel;

    private Integer comiteDependienteId;

    private Set<String> responsabilidadIds;

    private List<String> miembros;
}
