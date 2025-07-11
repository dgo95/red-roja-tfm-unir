package es.juventudcomunista.redroja.cjcrest.web.dto;

import es.juventudcomunista.redroja.cjccommonutils.enums.Rol;
import es.juventudcomunista.redroja.cjccommonutils.enums.Sexo;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MilitanteDTO {
    private String militanteId;
    private String apellido;
    private String apellido2;
    private LocalDate fechaNacimiento;
    private String nombre;
    private String numeroCarnet;
    private ComiteDTO comiteBase;
    private Sexo sexo;
    private List<ComiteDTO> comites;
    private Set<Rol> roles = new HashSet<>();
}

