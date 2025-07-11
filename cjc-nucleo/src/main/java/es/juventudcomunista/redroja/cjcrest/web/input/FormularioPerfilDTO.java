package es.juventudcomunista.redroja.cjcrest.web.input;

import es.juventudcomunista.redroja.cjcrest.dto.HabilidadDTO;
import es.juventudcomunista.redroja.cjcrest.dto.IdiomaConocidoDTO;
import es.juventudcomunista.redroja.cjcrest.enums.ExisteOrganoRepresentacionTrabajadores;
import es.juventudcomunista.redroja.cjcrest.enums.NivelIdioma;
import es.juventudcomunista.redroja.cjcrest.web.dto.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import es.juventudcomunista.redroja.cjccommonutils.enums.Sexo;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
public class FormularioPerfilDTO {
    //Basico
    private String email;
    private String telefono;
    private boolean premilitante;
    
    private int municipio;
    private int provincia;
    private int comunidadAutonoma;
   
    
    private String direccion;
    
    private boolean trabajador;
    private boolean estudiante;
    private boolean sindicado;
    
    private List<MunicipioDTO> municipios;
    private List<ProvinciaDTO> provincias;
    private List<ComunidadAutonomaDTO> comunidades;

    //Otros Datos
    private Map<Integer,String> mapaHabilidades;
    private List<HabilidadDTO> habilidades;

    private Map<Integer, NivelIdioma> mapaIdiomas;
    private List<IdiomaConocidoDTO> idiomas;
    
    
    //Estudiante
    private Integer nivelEducativo;
    private Integer subdivision;
    private Integer subsubdivision;
    
    private List<NivelEducativoDTO> nivelesEducativos;
    private List<SubdivisionNivelEducativoDTO> subdivisiones;
    private List<SubsubdivisionNivelEducativoDTO> subsubdivisiones;
    
    private String nombreEstudios;
    private String nombreCentroEducativo;
    private Integer anhoFinalizacion;
    private Boolean sindicatoEstudiantil;
    
    //Trabajador
    private Integer modalidadTrabajo;
    private Integer actividadEconomica;
    private Integer tipoContrato;
    private String profesion;
    private ExisteOrganoRepresentacionTrabajadores existeOrganoRepresentacionTrabajadores;
    private Boolean participaOrganoRepresentacion;
    
    private String nombreCentroTrabajo;
    private Integer numeroTrabajadoresCentroTrabajo;
    private String direccionCentroTrabajo;
    private String nombreEmpresa;
    private Integer numeroTrabajadores;
    private LocalDate fechaInicioContrato;
    
    private List<ActividadEconomicaDTO> actividadesEconomicas;
    private List<TipoContratoDTO> tiposContratos;
    private List<ModalidadTrabajoDTO> modalidadesTrabajo;
    
    //Sindicación
    private List<SindicatoDTO> sindicatos;
    private List<FederacionDTO> federaciones;
    private Integer sindicato;
    private Integer federacion;
    private String cargo;
    private Boolean participaAreaJuventud;
    private String sindicatoOtros;
    private String federacionOtros;

    // Campos básicos
    private String nombre;
    private String primerApellido;
    private String segundoApellido;
    private String numeroCarnet;
    private LocalDate fechaNacimiento;
    private Sexo sexo;

}