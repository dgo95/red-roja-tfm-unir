package es.juventudcomunista.redroja.cjcrest.dto;

import es.juventudcomunista.redroja.cjcrest.web.dto.ComunidadAutonomaDTO;
import es.juventudcomunista.redroja.cjcrest.web.dto.MunicipioDTO;
import es.juventudcomunista.redroja.cjcrest.web.dto.ProvinciaDTO;
import es.juventudcomunista.redroja.cjcrest.web.dto.ResponsableDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactoDTO {

    private Long id;

    private String nombre;

    private LocalDate fechaNacimiento;

    private Boolean estudiante;

    private Boolean trabajador;

    private String telefono;

    private String email;

    // Usamos el ID en lugar de la entidad Municipio
    private Integer municipio;
    private Integer provincia;
    private Integer comunidad;

    private String situacionOrigen;

    private String estadoActual;

    // Usamos el ID en lugar de la entidad Militante
    private String militanteId;

    private String proximaTarea;

    private List<ComunidadAutonomaDTO> comunidades;
    private List<ProvinciaDTO> provincias;
    private List<MunicipioDTO> municipios;
    private List<ResponsableDTO> responsables;
}