package es.juventudcomunista.redroja.cjcrest.web.input;

import es.juventudcomunista.redroja.cjcrest.web.response.NivelIdiomaDTO;
import lombok.Data;

import java.util.List;

@Data
public class OtrosDatosDTO {
    private String equipoFotografiaVideo;
    private String programasEdicionVideo;
    private String programasMaquetacion;
    private String herramientasManejoRSSCommunityManager;
    private String conocimientoWordpress;
    private String desarrolloWeb;
    private String conocimientosSEO;
    private List<NivelIdiomaDTO> idiomas;
    private String artesMarciales;
    private String habilidadInstrumentoCanto;
    private String conocimientosMusicales;
    private String estudiosMusicales;
    private String experienciasTocandoPublico;
    private String conocimientosDisenoGrafico;
    private String otrosConocimientosTecnicos;
}
