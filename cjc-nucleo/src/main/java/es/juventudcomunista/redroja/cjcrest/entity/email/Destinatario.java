package es.juventudcomunista.redroja.cjcrest.entity.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Destinatario {
    private String email;
    private Map<String,String> variables;

}
