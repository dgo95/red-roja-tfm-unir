package es.juventudcomunista.redroja.cjccommonutils.email;

import lombok.*;

import java.util.Map;

// Annotations
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailDetails {

    private String to;
    private Map<String,String> variables;
    private String subject;
    private String plantilla;
    private String attachment;

}