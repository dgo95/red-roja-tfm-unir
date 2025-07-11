package es.juventudcomunista.redroja.cjcrest.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubsubdivisionNivelEducativoDTO {
    private Integer id;
    private String nombre;
    private Integer padreId;
}

