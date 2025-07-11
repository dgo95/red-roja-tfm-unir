package es.juventudcomunista.redroja.cjcrest.web.response;

import es.juventudcomunista.redroja.cjcrest.enums.NivelIdioma;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NivelIdiomaDTO {
    private String idioma;
    private NivelIdioma nivel;
}
