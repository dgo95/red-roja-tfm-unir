package es.juventudcomunista.redroja.cjcrest.service;

import es.juventudcomunista.redroja.cjcrest.dto.FichaMovilidadInputDTO;
import es.juventudcomunista.redroja.cjcrest.dto.IdiomaConocidoDTO;
import es.juventudcomunista.redroja.cjcrest.dto.PromocionarMilitanteInput;
import es.juventudcomunista.redroja.cjcrest.entity.*;
import es.juventudcomunista.redroja.cjcrest.enums.NivelIdioma;
import es.juventudcomunista.redroja.cjcrest.exception.MilitanteNotFoundException;
import es.juventudcomunista.redroja.cjcrest.keycloak.dto.UserRep;
import es.juventudcomunista.redroja.cjcrest.keycloak.dto.service.KeycloakAdminService;
import es.juventudcomunista.redroja.cjcrest.mapper.*;
import es.juventudcomunista.redroja.cjcrest.repository.*;
import es.juventudcomunista.redroja.cjcrest.web.dto.*;
import es.juventudcomunista.redroja.cjcrest.web.input.FormularioPerfilDTO;
import es.juventudcomunista.redroja.cjcrest.web.input.MilitanteInputDTO;
import es.juventudcomunista.redroja.cjcrest.keycloak.service.KeycloakUserFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import es.juventudcomunista.redroja.cjccommonutils.dto.FichaMovilidadDatos;
import es.juventudcomunista.redroja.cjccommonutils.enums.Organizacion;
import es.juventudcomunista.redroja.cjccommonutils.enums.Rol;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class MilitanteService {

    private final MilitanteRepository militanteRepository;
    private final NivelEstudiosRepository nivelEstudiosRepository;
    private final SubsubdivisionNivelEducativoRepository subsubdivisionNivelEducativoRepository;
    private final SubdivisionNivelEducativoRepository subdivisionNivelEducativoRepository;
    private final EstudiosRepository estudiosRepository;
    private final DireccionService direccionService;
    private final TrabajoService trabajoService;
    private final SindicatoService sindicatoService;
    private final IdiomaRepository idiomaRepository;
    private final DireccionRepository direccionRepository;
    private final ColectivoRepository colectivoRepository;
    private final MilitanteRolComiteRepository militanteRolComiteRepository;
    private final HabilidadRepository habilidadRepository;
    private final MilitanteHabilidadRepository militanteHabilidadRepository;
    private final MilitanteIdiomaRepository militanteIdiomaRepository;
    private final MunicipioRepository municipioRepository;
    private final FichaMovilidadRepository fichaMovilidadRepository;

    private final KeycloakUserFactory keycloakUserFactory;
    private final KeycloakAdminService keycloakAdminService;

    public FormularioPerfilDTO inicializaFormularioPerfil(String id) throws MilitanteNotFoundException {
        log.debug("MilitanteService: inicializaFormularioPerfil entra con parámetro ID: {}", id);
        try {
            var militante = this.getByMilitanteId(id);
            var direccionOpt = Optional.ofNullable(militante.getDireccion());
            var municipio = direccionOpt.map(Direccion::getMunicipio).orElse(null);
            var provincia = Optional.ofNullable(municipio).map(Municipio::getProvincia).orElse(null);
            var comunidadAutonoma = Optional.ofNullable(provincia).map(Provincia::getComunidadAutonoma).orElse(null);

            String direccion = direccionOpt.map(Direccion::getDireccion).orElse("");

            int comunidadId = comunidadAutonoma != null ? comunidadAutonoma.getId() : 0;
            int provinciaId  = provincia          != null ? provincia.getId()          : 0;
            int municipioId  = municipio          != null ? municipio.getId()          : 0;

            // Listas para los selects
            List<Provincia> provincias = comunidadAutonoma != null
                    ? direccionService.obtenerProvinciasPorComunidad(comunidadAutonoma)
                    : Collections.emptyList();
            List<Municipio> municipios = provincia != null
                    ? direccionService.obtenerMunicipiosPorProvincia(provincia)
                    : Collections.emptyList();

            var estudios = estudiosRepository.findByMilitante(militante).orElseGet(() -> Estudios.builder()
                    .militante(militante)
                    .nombreEstudio("")
                    .sindicatoEstudiantil(false)
                    .centroEstudios("")
                    .build());
            log.trace("inicializaFormularioPerfil: Estudios encontrados: {}", estudios);
            log.debug("inicializaFormularioPerfil: Inicia conversión a DTOs ");


            // Convertimos las listas
            List<ProvinciaDTO> provinciasDTO = ProvinciaMapper.INSTANCE.toDTOs(provincias);
            List<MunicipioDTO> municipiosDTO = MunicipioMapper.INSTANCE.toDTOs(municipios);


            log.debug("inicializaFormularioPerfil: Finaliza conversión a DTOs");

            var contrato = trabajoService.obtenerContrato(militante);
            var sindicacion = sindicatoService.obtenerMilitancia(militante);
            List<FederacionDTO> federacionesDTO = List.of();
            var federacion = sindicacion.getFederacion();
            var sindicato = sindicacion.getSindicato();

            if (federacion != null || sindicato != null) {
                var targetSindicato = federacion != null ? federacion.getSindicato() : sindicato;
                federacionesDTO = FederacionMapper.INSTANCE.toDTOs(sindicatoService.getAllFederacionesBySindicato(targetSindicato));
            }

            List<Habilidad> habilidades = habilidadRepository.findAll();
            Set<MilitanteHabilidad> setHabilidades = militante.getMilitanteHabilidades() == null ? new HashSet<>(): militante.getMilitanteHabilidades();
            Map<Integer, String> mapa = new HashMap<>();
            for (MilitanteHabilidad militanteHabilidad : setHabilidades) {
                Integer habilidadId = militanteHabilidad.getHabilidad().getId();
                String descripcion = militanteHabilidad.getDescripcion();
                mapa.put(habilidadId, descripcion);
            }

            Set<MilitanteIdioma> setIdiomas = militante.getIdiomas() == null ? new HashSet<>() : militante.getIdiomas();
            List<IdiomaConocidoDTO> idiomas = convertirAIdiomasDTO(idiomaRepository.findAll());
            Map<Integer, NivelIdioma> mapaIdiomas = new HashMap<>();

            for (MilitanteIdioma mi : setIdiomas) {
                mapaIdiomas.put(mi.getIdiomaConocido().getId(), mi.getNivel());
            }


            var formularioPerfilDTO = FormularioPerfilDTO.builder()
                    .premilitante(militante.getPremilitante())
                    .email(militante.getEmail())
                    .telefono(militante.getTelefono())
                    .comunidadAutonoma(comunidadId)
                    .provincia(provinciaId)
                    .municipio(municipioId)
                    .provincias(provinciasDTO)
                    .municipios(municipiosDTO)
                    .estudiante(militante.getEstudiante() != null && militante.getEstudiante())
                    .trabajador(militante.getTrabajador() != null && militante.getTrabajador())
                    .direccion(direccion)
                    .subsubdivision(estudios.getSubSubdivisionNivelEducativo())
                    .subdivision(estudios.getSubdivisionNivelEducativo())
                    .nivelEducativo(estudios.getNivelEducativo())
                    .anhoFinalizacion(estudios.getAnhoFinalizacion())
                    .nombreCentroEducativo(estudios.getCentroEstudios())
                    .nombreEstudios(estudios.getNombreEstudio())
                    .sindicatoEstudiantil(estudios.isSindicatoEstudiantil())
                    .actividadEconomica(contrato.getActividadEconomica() != null ? contrato.getActividadEconomica().getId() : null)
                    .direccionCentroTrabajo(contrato.getDireccionTrabajo())
                    .existeOrganoRepresentacionTrabajadores(contrato.getExisteOrganoRepresentacionTrabajadores())
                    .fechaInicioContrato(contrato.getFechaInicio())
                    .modalidadTrabajo(contrato.getModalidadTrabajo() != null ? contrato.getModalidadTrabajo().getId() : null)
                    .nombreCentroTrabajo(contrato.getNombreCentroTrabajo())
                    .nombreEmpresa(contrato.getEmpresa())
                    .numeroTrabajadores(contrato.getNumeroTrabajadoresEmpresa())
                    .numeroTrabajadoresCentroTrabajo(contrato.getNumeroTrabajadoresCentroTrabajo())
                    .participaOrganoRepresentacion(contrato.getParticipaOrganoRepresentacion())
                    .profesion(contrato.getProfesion())
                    .tipoContrato(contrato.getTipoContrato() != null ? contrato.getTipoContrato().getId() : null)
                    .federaciones(federacionesDTO)
                    .sindicato(sindicacion.getFederacion() != null ? sindicacion.getFederacion().getSindicato().getId() : sindicacion.getSindicato().getId())
                    .federacion(sindicacion.getFederacion() != null ? sindicacion.getFederacion().getId() : -1)
                    .sindicatoOtros(sindicacion.getSindicatoOtros())
                    .federacionOtros(sindicacion.getFederacionOtros())
                    .cargo(sindicacion.getCargo())
                    .participaAreaJuventud(sindicacion.isParticipaAreaJuventud())
                    .sindicado(militante.getSindicado() != null && militante.getSindicado())
                    // Otros datos
                    .habilidades(HabilidadMapper.INSTANCE.toDTOs(habilidades))
                    .mapaHabilidades(mapa)
                    .mapaIdiomas(mapaIdiomas)
                    .idiomas(idiomas)
                    //Basicos
                    .nombre(militante.getNombre())
                    .primerApellido(militante.getApellido())
                    .segundoApellido(militante.getApellido2())
                    .sexo(militante.getSexo())
                    .fechaNacimiento(militante.getFechaNacimiento())
                    .numeroCarnet(militante.getNumeroCarnet())
                    .build();
            log.trace("inicializaFormularioPerfil: Formulario de perfil construido: {}", Optional.ofNullable(formularioPerfilDTO));
            log.debug("inicializaFormularioPerfil: Finaliza OK ");
            return rellenaSelects(formularioPerfilDTO);
        } catch (Exception e) {
            log.error("inicializaFormularioPerfil: Error al inicializar el formulario de perfil para el militante con ID: {}", id, e);
            throw e;
        }
    }

    public FormularioPerfilDTO inicializaFormularioPerfil(){
        // 1. Recupera la autenticación del contexto de seguridad
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken jwtAuth) || !authentication.isAuthenticated()) {
            log.error("Intento de inicializar formulario sin usuario JWT válido");
            throw new UsernameNotFoundException("Usuario no autenticado correctamente");
        }

        // 2. Extrae el token y obtiene el claim "militanteId"
        var jwt = jwtAuth.getToken();
        String militanteId = jwt.getClaimAsString("militanteId");
        if (militanteId == null || militanteId.isBlank()) {
            log.error("Claim 'militanteId' no encontrado en el JWT: {}", jwt.getClaims());
            throw new UsernameNotFoundException("militanteId no presente en el token");
        }

        // 3. Llama al método que realmente inicializa el formulario usando ese ID
        return inicializaFormularioPerfil(militanteId);
    }

    private FormularioPerfilDTO rellenaSelects(FormularioPerfilDTO f){
        List<NivelEducativoDTO> niveles = NivelEducativoMapper.INSTANCE.toDTOs(nivelEstudiosRepository.findAll());
        List<SubdivisionNivelEducativoDTO> sub = SubdivisionNivelEducativoMapper.INSTANCE.toDTOs(subdivisionNivelEducativoRepository.findAll());
        List<SubsubdivisionNivelEducativoDTO> subsub = SubsubdivisionNivelEducativoMapper.INSTANCE.toDTOs(subsubdivisionNivelEducativoRepository.findAll());
        List<ComunidadAutonomaDTO> comunidadesDTO = ComunidadAutonomaMapper.INSTANCE.toDTOs(direccionService.obtenerTodasLasComunidadesAutonomas());

        List<ActividadEconomicaDTO> actividadesEconomicasDTO = ActividadEconomicaMapper.INSTANCE.toDTOs(trabajoService.obtenerTodasLasActividadesEconomicas());
        List<TipoContratoDTO> tiposContratosDTO = TipoContratoMapper.INSTANCE.toDTOs(trabajoService.obtenerTodosTiposContrato());
        List<ModalidadTrabajoDTO> modalidadesTrabajoDTO = ModalidadTrabajoMapper.INSTANCE.toDTOs(trabajoService.obtenerTodasModalidadesTrabajo());

        List<SindicatoDTO> sindicatosDTO = SindicatoMapper.INSTANCE.toDTOs(sindicatoService.getAllSindicatos());

        f.setNivelesEducativos(niveles);
        f.setSubdivisiones(sub);
        f.setSubsubdivisiones(subsub);
        f.setComunidades(comunidadesDTO);
        f.setActividadesEconomicas(actividadesEconomicasDTO);
        f.setTiposContratos(tiposContratosDTO);
        f.setModalidadesTrabajo(modalidadesTrabajoDTO);
        f.setSindicatos(sindicatosDTO);


        return f;
    }

    private List<IdiomaConocidoDTO> convertirAIdiomasDTO(List<IdiomaConocido> all) {
        return all.stream()
                .map(idioma -> IdiomaConocidoDTO.builder()
                        .id(idioma.getId())
                        .nombre(capitalizeFirstLetter(getNombreIdiomaPreferido(idioma)))
                        .build()
                )
                .collect(Collectors.toList());
    }

    private String getNombreIdiomaPreferido(IdiomaConocido idioma) {
        return idioma.getLangES();
    }

    private String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    public FormularioPerfilDTO actualizaFormularioPerfil(String id, DatosBasicosDTO datos)
            throws MilitanteNotFoundException {
        var m = getByMilitanteId(id);
        m.setEmail(datos.getEmail());
        m.setTelefono(datos.getTelefono());
        m.setEstudiante(actualizaEstudiante(m, datos.getEsEstudiante()));
        m.setTrabajador(actualizaContrato(m, datos.getEsTrabajador()));
        m.setSindicado(actualizaSindicacion(m, datos.getSindicado()));

        var mapa = datos.getHabilidades();
        var mapaIdioma = datos.getIdiomas();

        //Eliminar idiomas no presentes en el nuevo mapa
        var idiomasParaEliminar = m.getIdiomas().stream()
                .filter(militanteIdioma -> !mapaIdioma.containsKey(militanteIdioma.getIdiomaConocido().getId()))
                .toList();

        idiomasParaEliminar.forEach(militanteIdioma -> {
            m.getMilitanteHabilidades().remove(militanteIdioma);
            militanteIdiomaRepository.delete(militanteIdioma);
        });

        // Eliminar habilidades no presentes en el nuevo mapa
        var habilidadesParaEliminar = m.getMilitanteHabilidades().stream()
                .filter(militanteHabilidad -> !mapa.containsKey(militanteHabilidad.getHabilidad().getId()))
                .toList();

        habilidadesParaEliminar.forEach(militanteHabilidad -> {
            m.getMilitanteHabilidades().remove(militanteHabilidad);
            militanteHabilidadRepository.delete(militanteHabilidad);
        });
        // Recorrer el mapa de idiomas
        mapaIdioma.forEach((clave, valor) -> {
            var idioma = idiomaRepository.findById(clave).orElseThrow(EntityNotFoundException::new);
            var militanteIdioma = militanteIdiomaRepository.findByMilitanteAndIdiomaConocido(m, idioma)
                    .orElseGet(() -> MilitanteIdioma.builder()
                            .militante(m)
                            .idiomaConocido(idioma)
                            .build());

            militanteIdioma.setNivel(valor);
            m.getIdiomas().add(militanteIdioma);
            militanteIdiomaRepository.save(militanteIdioma);
        });

        // Recorrer el mapa de habilidades
        mapa.forEach((clave, valor) -> {
            var habilidad = habilidadRepository.findById(clave).orElseThrow(EntityNotFoundException::new);
            var militanteHabilidad = militanteHabilidadRepository.findByMilitanteAndHabilidad(m, habilidad)
                    .orElseGet(() -> MilitanteHabilidad.builder()
                            .militante(m)
                            .habilidad(habilidad)
                            .build());

            militanteHabilidad.setDescripcion(valor);
            m.getMilitanteHabilidades().add(militanteHabilidad);
            militanteHabilidadRepository.save(militanteHabilidad);
        });

        Direccion d = Optional.ofNullable(m.getDireccion())
                .orElseGet(() -> {
                    Direccion nueva = Direccion.builder().build();
                    m.setDireccion(nueva);
                    return nueva;
                });
        d.setDireccion(datos.getDireccion());
        d.setMunicipio(direccionService.getMunicipioById(datos.getMunicipio()));
        direccionService.guardar(d);
        militanteRepository.save(m);
        return inicializaFormularioPerfil(id);
    }

    private Boolean actualizaSindicacion(Militante m, Boolean sindicado) {
        if (Boolean.FALSE.equals(sindicado)) {
            sindicatoService.borraSindicacion(m);
        }
        return sindicado;
    }

    private Boolean actualizaContrato(Militante m, Boolean esTrabajador) {
        if (Boolean.FALSE.equals(esTrabajador)) {
            trabajoService.borraContrato(m);
        }
        return esTrabajador;
    }

    private Boolean actualizaEstudiante(Militante m, @NotNull Boolean esEstudiante) {
        if (Boolean.FALSE.equals(esEstudiante) && estudiosRepository.existsByMilitante(m)) {
            estudiosRepository.deleteByMilitante(m);
        }

        return esEstudiante;
    }

    public FormularioPerfilDTO actualizaFormularioPerfil(String id, @Valid DatosEstudioDTO datos) throws MilitanteNotFoundException {
        var m = getByMilitanteId(id);

        var estudios = estudiosRepository.findByMilitante(m).orElseGet(() -> Estudios.builder()
                .militante(m)
                .build());

        estudios.setAnhoFinalizacion(datos.getAnhoFinalizacion());
        estudios.setCentroEstudios(datos.getNombreCentroEducativo());
        estudios.setNombreEstudio(datos.getNombreEstudios());
        estudios.setSindicatoEstudiantil(datos.getSindicatoEstudiantil());
        estudios.setNivelEducativo(datos.getNivelEstudio());
        estudios.setSubdivisionNivelEducativo(datos.getTipoEstudio());
        estudios.setSubSubdivisionNivelEducativo(datos.getSubtipoEstudio());
        estudiosRepository.save(estudios);

        return inicializaFormularioPerfil(id);
    }

    public FormularioPerfilDTO actualizaFormularioPerfil(String id, @Valid DatosTrabajoDTO datos)
            throws MilitanteNotFoundException {
        var m = getByMilitanteId(id);

        var contrato = trabajoService.obtenerContrato(m);

        // Asignar la actividad económica al contrato
        contrato.setActividadEconomica(trabajoService.obtenerActividadEconomica(datos.getActividadEconomica()));

        // Asignar el tipo de contrato al contrato
        contrato.setTipoContrato(trabajoService.obtenerTipoContrato(datos.getTipoContrato()));

        // Asignar la modalidad de trabajo al contrato
        contrato.setModalidadTrabajo(trabajoService.obtenerModalidadTrabajo(datos.getModalidadTrabajo()));

        contrato.setProfesion(datos.getProfesion());
        contrato.setExisteOrganoRepresentacionTrabajadores(datos.getExisteOrganoRepresentacion());
        contrato.setParticipaOrganoRepresentacion(datos.getParticipaOrganoRepresentacion());
        if (contrato.getModalidadTrabajo().getNombre().equals("Teletrabajo")) {
            contrato.setNombreCentroTrabajo(null);
            contrato.setNumeroTrabajadoresCentroTrabajo(null);
            contrato.setDireccionTrabajo(null);
        } else {
            contrato.setNombreCentroTrabajo(datos.getNombreCentroTrabajo());
            contrato.setNumeroTrabajadoresCentroTrabajo(datos.getNumeroTrabajadoresCentroTrabajo());
            contrato.setDireccionTrabajo(datos.getDireccionCentroTrabajo());
        }
        contrato.setEmpresa(datos.getNombreEmpresa());
        contrato.setNumeroTrabajadoresEmpresa(datos.getNumeroTrabajadores());
        contrato.setFechaInicio(datos.getFechaInicioContrato());

        trabajoService.guardaContrato(contrato);

        return inicializaFormularioPerfil(id);
    }

    public FormularioPerfilDTO actualizaFormularioPerfil(String id, @Valid DatosSindicacionDTO datos)
            throws MilitanteNotFoundException {
        log.debug("Iniciando actualización del perfil del militante con ID: {}", id);

        var m = getByMilitanteId(id);
        log.debug("Militante obtenido: {}", m);

        Sindicacion sindicacion = sindicatoService.obtenerMilitancia(m);
        log.debug("Sindicacion obtenida: {}", sindicacion);

        sindicacion.setCargo(datos.getCargo());
        log.debug("Cargo establecido: {}", datos.getCargo());

        sindicacion.setParticipaAreaJuventud(datos.getParticipaAreaJuventud() != null && datos.getParticipaAreaJuventud());
        log.debug("Participa en área de juventud: {}", datos.getParticipaAreaJuventud());

        final Integer VALOR_NO_SELECCIONADO = -1;
        Integer sindicatoId = datos.getSindicato();
        Integer federacionId = datos.getFederacion();

        log.debug("Datos recibidos - Sindicato ID: {}, Federación ID: {}", sindicatoId, federacionId);

        boolean sindicatoSeleccionado = !sindicatoId.equals(VALOR_NO_SELECCIONADO);
        boolean federacionSeleccionada = !federacionId.equals(VALOR_NO_SELECCIONADO);

        log.debug("Sindicato seleccionado: {}, Federación seleccionada: {}", sindicatoSeleccionado, federacionSeleccionada);

        if (!sindicatoSeleccionado && !federacionSeleccionada) {
            log.debug("Configuración de sindicacion sin selección de sindicato ni federación.");
            configurarSindicacionSinSeleccion(sindicacion, datos);
        } else if (sindicatoSeleccionado && !federacionSeleccionada) {
            log.debug("Configuración de sindicacion con sindicato seleccionado.");
            configurarSindicacionConSindicato(sindicacion, sindicatoId, datos);
        } else if (sindicatoSeleccionado) {
            log.debug("Configuración de sindicacion con federación seleccionada.");
            configurarSindicacionConFederacion(sindicacion, federacionId);
        } else {
            log.error("Combinación inválida de Sindicato y Federación - Sindicato ID: {}, Federación ID: {}", sindicatoId, federacionId);
            throw new IllegalArgumentException("Combinación inválida de Sindicato y Federación");
        }

        sindicatoService.actualizaSindicacion(sindicacion);
        log.debug("Sindicacion actualizada: {}", sindicacion);

        return inicializaFormularioPerfil(id);
    }

    private void configurarSindicacionSinSeleccion(Sindicacion sindicacion, DatosSindicacionDTO datos) {
        sindicacion.setFederacion(null);
        sindicacion.setSindicato(null);
        sindicacion.setSindicatoOtros(datos.getSindicatoOtros());
        sindicacion.setFederacionOtros(datos.getFederacionOtros());
    }

    private void configurarSindicacionConSindicato(Sindicacion sindicacion, Integer sindicatoId, DatosSindicacionDTO datos) {
        sindicacion.setSindicato(sindicatoService.getSindicatoById(sindicatoId));
        sindicacion.setFederacion(null);
        sindicacion.setSindicatoOtros(null);
        sindicacion.setFederacionOtros(datos.getFederacionOtros());
    }

    private void configurarSindicacionConFederacion(Sindicacion sindicacion, Integer federacionId) {
        sindicacion.setFederacion(sindicatoService.getFederacionById(federacionId));
        sindicacion.setSindicato(null);
        sindicacion.setSindicatoOtros(null);
        sindicacion.setFederacionOtros(null);
    }

    public MilitanteDTO obtieneDatosBasicos(String id) throws MilitanteNotFoundException {
        var m = getByMilitanteId(id);

        var c = m.getComitesBase().stream().findFirst().orElseThrow();
        var cb = ComiteDTO.builder()
                .id(c.getId())
                .nombre(c.getNombre())
                .build();

        return MilitanteDTO.builder()
                .nombre(m.getNombre())
                .apellido(m.getApellido())
                .apellido2(m.getApellido2())
                .numeroCarnet(m.getNumeroCarnet())
                .fechaNacimiento(m.getFechaNacimiento())
                .militanteId(m.getMilitanteId())
                .comiteBase(cb)
                .sexo(m.getSexo())
                .comites(crearListaDTO(m.getComitesBase().stream().findFirst().orElseThrow()))
                .build();
    }

    private List<ComiteDTO> crearListaDTO(ComiteBase c) {
        List<ComiteDTO> comites = new ArrayList<>();

        var comite = c.getComiteDependiente();

        while (!comite.equals(comite.getComiteDependiente())) {
            var actual = ComiteDTO.builder()
                    .id(comite.getId())
                    .nombre(comite.getNombre())
                    .build();
            comites.add(actual);
            comite = comite.getComiteDependiente();
        }

        var central = ComiteDTO.builder()
                .id(comite.getId())
                .nombre(comite.getNombre())
                .build();
        comites.add(central);

        return comites;
    }

    private Militante getByMilitanteId(String id) throws MilitanteNotFoundException {
        log.debug("getById ID: {}", id);
        var m = militanteRepository
                .findByMilitanteId(id)
                .orElseThrow(() -> {
                    log.warn("getById: Militante con id {} no encontrado", id);
                    return new MilitanteNotFoundException("Militante con id " + id + " no encontrado");
                });
        log.trace("getById: Militante encontrado: {}", m);
        log.debug("getById: OK para: {}", id);
        return m;
    }

    @Transactional
    public MilitanteDTO crear(MilitanteInputDTO dto) {

        // Si es un premilitante no puede ser activo
        if (dto.getPremilitante()) {
            dto.setActivar(Boolean.FALSE);
        }

        if (dto.getNumeroCarnet() != null && dto.getNumeroCarnet().length() < 4) {
            dto.setNumeroCarnet(String.format("%04d", Integer.parseInt(dto.getNumeroCarnet())));
        }

        var direccionDto = dto.getDireccion();

        var municipio = direccionService.getMunicipioById(direccionDto.getMunicipioId());
        var entityDireccion = Direccion.builder()
                .direccion(direccionDto.getDireccion())
                .codigoPostal(direccionDto.getCodigoPostal())
                .municipio(municipio)
                .build();
        direccionRepository.saveAndFlush(entityDireccion);

        if (null == dto.getComiteBaseId()) {
            dto.setComiteBaseId(dto.getOrganizacion().equals(Organizacion.PCTE) ? 1 : 2); //1 (o 2) porque es el comiteBase por defecto
        }

        Set<Rol> roles = new HashSet<>();
        roles.add(Rol.MIEMBRO);
        if (dto.getRoles().contains(Rol.ADMIN) && !dto.getPremilitante()) {
            roles.add(Rol.ADMIN);
        }


        var colectivo = colectivoRepository.findById(dto.getComiteBaseId()).orElseThrow();

        var entity = Militante.builder()
                .apellido(dto.getApellido())
                .apellido2(dto.getApellido2())
                .nombre(dto.getNombre())
                .numeroCarnet(dto.getNumeroCarnet())
                .fechaNacimiento(dto.getFechaNacimiento())
                .email(dto.getEmail())
                .telefono(dto.getTelefono())
                .estudiante(dto.getEstudiante())
                .trabajador(dto.getTrabajador())
                .sindicado(dto.getSindicado())
                .sexo(dto.getSexo())
                .direccion(entityDireccion)
                .comitesBase(Set.of(colectivo))
                .organizacion(dto.getOrganizacion())
                .premilitante(dto.getPremilitante())
                .build();

        militanteRepository.saveAndFlush(entity);

        Set<MilitanteRolComite> responsabilidades = roles.stream()
                .map(rol -> rolAMilitanteRolComite(rol, entity))
                .collect(Collectors.toSet());

        militanteRolComiteRepository.saveAll(responsabilidades);

        if(Boolean.TRUE.equals(dto.getTrabajador())){
            getionaDatosTrabajo(entity.getMilitanteId(),dto);
        }

        if(Boolean.TRUE.equals(dto.getEstudiante())){
            gestionDatosEstudio(entity.getMilitanteId(),dto);
        }

        if(Boolean.TRUE.equals(dto.getSindicado())){
            gestionDatosSindicacion(entity.getMilitanteId(),dto);
        }

        if(Boolean.TRUE.equals(dto.getActivar())){
            activarMilitante(entity.getMilitanteId());
        }

        return MilitanteMapper.INSTANCE.toDTO(entity);
    }

    @Transactional
    public void activarMilitante(String militanteId) {
        var entity = getByMilitanteId(militanteId);
        if(Boolean.TRUE.equals(entity.getPremilitante())){
            throw new RuntimeException("Los premilitantes no se pueden activar");
        }
        UserRep rep   = keycloakUserFactory.build(entity);
        String userId = keycloakAdminService.createUser(rep);
        
        var colectivos = entity.getComitesBase();
        // Asigna el usuario al grupo de su celula/colectivo
        colectivos.forEach(colectivo -> {
            var grupo = keycloakAdminService.findGroupByPath(colectivo.getFullPath());
            keycloakAdminService.addUserToGroup(userId,grupo.getId());
        });
        keycloakAdminService.executeActionsEmail(
                userId,
                List.of("VERIFY_EMAIL","UPDATE_PASSWORD"),
                12 * 60 * 60,
                null,
                null
        );
    }

    private void gestionDatosSindicacion(String militanteId, MilitanteInputDTO dto) {
        var dato = DatosSindicacionDTO.builder()
                .cargo(dto.getCargo())
                .federacion(dto.getFederacion())
                .federacionOtros(dto.getFederacionOtros())
                .participaAreaJuventud(dto.getParticipaAreaJuventud())
                .sindicato(dto.getSindicato())
                .sindicatoOtros(dto.getSindicatoOtros())
                .build();
        actualizaFormularioPerfil(militanteId,dato);
    }

    private void gestionDatosEstudio(String militanteId, MilitanteInputDTO militanteInputDTO) {
        var dato = DatosEstudioDTO.builder()
                .tipoEstudio(militanteInputDTO.getTipoEstudio())
                .subtipoEstudio(militanteInputDTO.getSubtipoEstudio())
                .nombreCentroEducativo(militanteInputDTO.getNombreCentroEducativo())
                .nivelEstudio(militanteInputDTO.getNivelEstudio())
                .nombreEstudios(militanteInputDTO.getNombreEstudios())
                .anhoFinalizacion(militanteInputDTO.getAnhoFinalizacion())
                .sindicatoEstudiantil(militanteInputDTO.getSindicatoEstudiantil())
                .build();
        actualizaFormularioPerfil(militanteId,dato);
    }

    private void getionaDatosTrabajo(String militanteId, MilitanteInputDTO militanteInputDTO) {
        var dato = DatosTrabajoDTO.builder()
                .actividadEconomica(militanteInputDTO.getActividadEconomica())
                .tipoContrato(militanteInputDTO.getTipoContrato())
                .modalidadTrabajo(militanteInputDTO.getModalidadTrabajo())
                .profesion(militanteInputDTO.getProfesion())
                .existeOrganoRepresentacion(militanteInputDTO.getExisteOrganoRepresentacion())
                .participaOrganoRepresentacion(militanteInputDTO.getParticipaOrganoRepresentacion() != null && militanteInputDTO.getParticipaOrganoRepresentacion())
                .nombreCentroTrabajo(militanteInputDTO.getNombreCentroTrabajo())
                .numeroTrabajadoresCentroTrabajo(militanteInputDTO.getNumeroTrabajadoresCentroTrabajo())
                .direccionCentroTrabajo(militanteInputDTO.getDireccionCentroTrabajo())
                .nombreEmpresa(militanteInputDTO.getNombreEmpresa())
                .numeroTrabajadores(militanteInputDTO.getNumeroTrabajadores())
                .fechaInicioContrato(militanteInputDTO.getFechaInicioContrato())
                .build();
        actualizaFormularioPerfil(militanteId,dato);
    }

    private MilitanteRolComite rolAMilitanteRolComite(Rol rol, Militante entity) {
        return MilitanteRolComite.builder()
                .rol(rol)
                .militante(entity)
                .comiteBase(entity.getComitesBase().stream().findAny().orElseThrow())
                .build();
    }

    public MilitanteDTO obtenerPorId(String id) {
        var militante = militanteRepository.findByMilitanteId(id).orElseThrow(() -> new UsernameNotFoundException("Militante no encontrado con militanteId: " + id));
        return MilitanteMapper.INSTANCE.toDTO(militante);
    }

    public List<MilitanteDTO> obtenerTodos() {
        var militantes = militanteRepository.findAll();
        return MilitanteMapper.INSTANCE.toDTOs(militantes);
    }

    @Transactional
    public void  eliminar(String id) {
        Optional<Militante> militanteOptional = militanteRepository.findByMilitanteId(id);
        if (militanteOptional.isPresent()) {
            Militante militante = militanteOptional.get();
            UserRep rep   = keycloakAdminService.findUserByMilitanteId(militante.getMilitanteId());
            log.info("Eliminando militante con id: {}", rep.getId());

            List<MilitanteRolComite> lista = militanteRolComiteRepository.findByMilitante(militante);
            lista.forEach(e -> militante.getResponsabilidades().remove(e));
            lista.forEach(e -> e.getComiteBase().getResponsabilidades().remove(e));
            militanteRolComiteRepository.deleteAll(lista);

            for (ComiteBase comiteBase : militante.getComitesBase()) {
                comiteBase.getMilitantes().remove(militante);
            }
            militante.getComitesBase().clear();
            militanteRepository.delete(militante);

            keycloakAdminService.deleteUser(rep.getId());
        }
    }

    public MilitanteDTO editar(String id, MilitanteInputDTO militanteDTO) {
        log.debug("Editar militante con ID: {}", id);
        var militante = getByMilitanteId(id);

        // Actualizar datos básicos
        militante.setNombre(militanteDTO.getNombre());
        militante.setApellido(militanteDTO.getApellido());
        militante.setApellido2(militanteDTO.getApellido2());
        militante.setEmail(militanteDTO.getEmail());
        militante.setTelefono(militanteDTO.getTelefono());
        militante.setSexo(militanteDTO.getSexo());
        militante.setFechaNacimiento(militanteDTO.getFechaNacimiento());
        militante.setNumeroCarnet(militanteDTO.getNumeroCarnet());
        militante.setEstudiante(militanteDTO.getEstudiante());
        militante.setTrabajador(militanteDTO.getTrabajador());
        militante.setSindicado(militanteDTO.getSindicado());

        // Actualizar dirección
        var direccionDto = militanteDTO.getDireccion();
        var municipio = direccionService.getMunicipioById(direccionDto.getMunicipioId());
        var direccion = militante.getDireccion();
        direccion.setDireccion(direccionDto.getDireccion());
        direccion.setCodigoPostal(direccionDto.getCodigoPostal());
        direccion.setMunicipio(municipio);
        direccionRepository.saveAndFlush(direccion);


        // Actualizar datos de trabajo si es trabajador
        if (Boolean.TRUE.equals(militanteDTO.getTrabajador())) {
            getionaDatosTrabajo(militante.getMilitanteId(), militanteDTO);
        } else {
            trabajoService.borraContrato(militante);
        }

        // Actualizar datos de estudio si es estudiante
        if (Boolean.TRUE.equals(militanteDTO.getEstudiante())) {
            gestionDatosEstudio(militante.getMilitanteId(), militanteDTO);
        } else {
            estudiosRepository.deleteByMilitante(militante);
        }

        // Actualizar datos de sindicación si está sindicado
        if (Boolean.TRUE.equals(militanteDTO.getSindicado())) {
            gestionDatosSindicacion(militante.getMilitanteId(), militanteDTO);
        } else {
            sindicatoService.borraSindicacion(militante);
        }

        // Guardar militante actualizado
        militanteRepository.saveAndFlush(militante);

        log.debug("Militante editado con éxito con ID: {}", id);
        return MilitanteMapper.INSTANCE.toDTO(militante);
    }

    public FichaMovilidad crearFichaMovilidad(String militanteId, FichaMovilidadInputDTO dto) {
        var militante = getByMilitanteId(militanteId);

        if (militante.getFichaMovilidad() != null) {
            throw new IllegalStateException("El militante ya tiene una ficha de movilidad asignada");
        }

        Municipio m = municipioRepository.findById(dto.getMunicipio()).orElseThrow();

        var fichaMovilidad = FichaMovilidad.builder()
                .militante(militante)
                .fechaInicio(dto.getFechaInicio())
                .fechaFin(dto.getFechaFin())
                .municipio(m)
                .objetoTraslado(dto.getObjetoTraslado())
                .build();

        militante.setFichaMovilidad(fichaMovilidad);
        militanteRepository.save(militante); // Guarda el militante junto con la ficha de movilidad

        return fichaMovilidad;
    }

    public FichaMovilidadInputDTO editarFichaMovilidad(String militanteId, FichaMovilidadInputDTO dto) {
        var militante = getByMilitanteId(militanteId);

        var fichaMovilidad = obtenerFichaMovilidad(militanteId);

        if (fichaMovilidad == null) {
            throw new EntityNotFoundException("El militante no tiene una ficha de movilidad asociada");
        }
        Municipio m = municipioRepository.findById(dto.getMunicipio()).orElseThrow();

        fichaMovilidad.setFechaInicio(dto.getFechaInicio());
        fichaMovilidad.setFechaFin(dto.getFechaFin());
        fichaMovilidad.setMunicipio(m);
        fichaMovilidad.setObjetoTraslado(dto.getObjetoTraslado());

        fichaMovilidad.setFrentesTrabajo(dto.getFrentesTrabajo());
        fichaMovilidad.setSindicatoEstudiantil(dto.getSindicatoEstudiantil());

        fichaMovilidad.setOtrasResponsabilidades(dto.getOtrasResponsabilidades());
        fichaMovilidad.setResponsabilidadDestacada(dto.getResponsabilidadDestacada());
        fichaMovilidad.setPuntosPositivos(dto.getPuntosPositivos());
        fichaMovilidad.setHabitosMejorar(dto.getHabitosMejorar());

        fichaMovilidad.setOtrasObservaciones(dto.getOtrasObservaciones());


        militante.setFichaMovilidad(fichaMovilidad);

        militanteRepository.save(militante); // Guarda el militante con la ficha de movilidad actualizada

        return FichaMovilidadMapper.INSTANCE.toDTO(fichaMovilidad);
    }

    public FichaMovilidad obtenerFichaMovilidad(String militanteId) {
        var militante = getByMilitanteId(militanteId);

        var fichaMovilidad = Optional.ofNullable(militante.getFichaMovilidad())
                .orElseGet(() -> crearFichaMovilidadPorDefecto(militante));


        if (fichaMovilidad == null) {
            throw new EntityNotFoundException("El militante no tiene una ficha de movilidad asociada");
        }

        return fichaMovilidad;
    }

    private FichaMovilidad crearFichaMovilidadPorDefecto(Militante militante) {
        LocalDate fechaInicio = LocalDate.now();
        LocalDate fechaFin    = fechaInicio.plusMonths(1);

        Comite comiteResponsable = militante.getComitesBase()
                .stream()
                .findFirst()
                .map(ComiteBase::getComiteDependiente)
                .orElse(null);   // mejor manejar ausencia según reglas de dominio

        return FichaMovilidad.builder()
                .militante(militante)
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .comiteResponsable(comiteResponsable)
                .build();
    }


    public FichaMovilidadInputDTO obtenerFichaMovilidadPorMilitanteId(String militanteId) {

        var fichaMovilidad = obtenerFichaMovilidad(militanteId);

        return cargaDTO(fichaMovilidad);
    }

    private FichaMovilidadInputDTO cargaDTO(FichaMovilidad ficha) {

        var dto = FichaMovilidadMapper.INSTANCE.toDTO(ficha);
        if(ficha.getMunicipio() == null){
            var comunidades = direccionService.obtenerTodasLasComunidadesAutonomas();
            var comunidadesDTOS = ComunidadAutonomaMapper.INSTANCE.toDTOs(comunidades);
            dto.setComunidades(comunidadesDTOS);
        }else{
            var municipios = direccionService.obtenerMunicipiosPorProvinciaId(ficha.getMunicipio().getProvincia().getId());
            var provincias = direccionService.obtenerProvinciasPorComunidadId(ficha.getMunicipio().getProvincia().getComunidadAutonoma().getId());
            var comunidades = direccionService.obtenerTodasLasComunidadesAutonomas();
            var comunidadesDTOS = ComunidadAutonomaMapper.INSTANCE.toDTOs(comunidades);
            dto.setComunidades(comunidadesDTOS);
            dto.setProvincias(provincias);
            dto.setMunicipios(municipios);
            dto.setMunicipio(ficha.getMunicipio().getId());
            dto.setProvincia(ficha.getMunicipio().getProvincia().getId());
            dto.setComunidadAutonoma(ficha.getMunicipio().getProvincia().getComunidadAutonoma().getId());
        }
        return dto;
    }

    public void eliminarFichaMovilidad(String militanteId) {
        var militante = getByMilitanteId(militanteId);

        if (militante.getFichaMovilidad() == null) {
            throw new EntityNotFoundException("El militante no tiene una ficha de movilidad asociada");
        }

        fichaMovilidadRepository.delete(militante.getFichaMovilidad());
        militante.setFichaMovilidad(null); // Desasocia la ficha de movilidad del militante
        militanteRepository.save(militante); // Guarda el militante con la ficha de movilidad eliminada
    }

    public FichaMovilidadDatos obtenerFichaMovilidadDatosPorMilitanteId(String militanteId) {
        var militante = getByMilitanteId(militanteId);
        var ficha = militante.getFichaMovilidad();

        if (ficha == null) {
            return null;
        }

        String nombreCompleto = formatNombreCompleto(militante);
        String tiempoEstancia = formatTiempoEstancia(ficha);
        String residencia = formatResidencia(ficha);
        String territorioProcedencia = formatTerritorioProcedencia(militante, ficha);
        String responsabilidades = formatResponsabilidades(militante);
        String habilidades = formatHabilidades(militante);

        return FichaMovilidadDatos.builder()
                .nombreCompleto(nombreCompleto)
                .email(militante.getEmail())
                .telefono(militante.getTelefono())
                .tiempoEstancia(tiempoEstancia)
                .objetoTraslado(ficha.getObjetoTraslado())
                .residencia(residencia)
                .frentesTrabajo(ficha.getFrentesTrabajo())
                .sindicatoEstudiantil(ficha.getSindicatoEstudiantil())
                .territorioProcedencia(territorioProcedencia)
                .responsabilidadActual(responsabilidades)
                .otrasResponsabilidades(ficha.getOtrasResponsabilidades())
                .responsabilidadDestacada(ficha.getResponsabilidadDestacada())
                .habilidades(habilidades)
                .puntosPositivos(ficha.getPuntosPositivos())
                .habitosMejorar(ficha.getHabitosMejorar())
                .otrasObservaciones(ficha.getOtrasObservaciones())
                .build();
    }

    private String formatNombreCompleto(Militante militante) {
        String apellido2 = militante.getApellido2() != null ? militante.getApellido2() : "";
        return militante.getNombre() + " " + militante.getApellido() + " " + apellido2;
    }

    private String formatTiempoEstancia(FichaMovilidad ficha) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fechaFin = ficha.getFechaFin() != null ? ficha.getFechaFin().format(formatter) : "Indeterminado";
        return ficha.getFechaInicio().format(formatter) + " - " + fechaFin;
    }

    private String formatResidencia(FichaMovilidad ficha) {
        var municipio = ficha.getMunicipio();
        var provincia = municipio.getProvincia();
        var comunidadAutonoma = provincia.getComunidadAutonoma();
        return String.format("%s, %s, %s", municipio.getNombre(), provincia.getNombre(), comunidadAutonoma.getNombre());
    }

    private String formatTerritorioProcedencia(Militante militante, FichaMovilidad ficha) {
        ComiteBase comiteBase = militante.getComitesBase().stream().findFirst().orElseThrow();
        StringBuilder territorio = new StringBuilder(comiteBase.getNombre());

        Comite comite = comiteBase.getComiteDependiente();
        while (comite != null && !Objects.equals(comite.getId(), ficha.getComiteResponsable().getId())) {
            territorio.append(", ").append(comite.getNombre());
            comite = comite.getComiteDependiente();
        }

        return territorio.toString();
    }

    private String formatResponsabilidades(Militante militante) {
        StringBuilder responsabilidades = new StringBuilder();

        militante.getResponsabilidades().forEach(responsabilidad -> {
            if(!responsabilidad.getRol().equals(Rol.ADMIN) && !responsabilidad.getRol().equals(Rol.MIEMBRO)){
                String comiteNombre = responsabilidad.getComite() != null
                        ? responsabilidad.getComite().getNombre()
                        : responsabilidad.getComiteBase().getNombre();
                responsabilidades.append(responsabilidad.getRol())
                        .append(" (")
                        .append(comiteNombre)
                        .append(") ");
            }
        });

        return responsabilidades.toString().trim();
    }

    private String formatHabilidades(Militante militante) {
        StringBuilder habilidades = new StringBuilder();

        militante.getMilitanteHabilidades().forEach(habilidad -> habilidades.append(habilidad.getHabilidad().getNombre())
                .append(": ")
                .append(habilidad.getDescripcion())
                .append(" "));

        return habilidades.toString().trim();
    }

    public void promocionarMilitante(String militanteId, PromocionarMilitanteInput dto) {
        var m = getByMilitanteId(militanteId);

        if(!m.getPremilitante()){
            throw new RuntimeException("Ya es militante");
        }
        m.setPremilitante(Boolean.FALSE);
        m.setNumeroCarnet(dto.getNumeroCarnet());
        if(Boolean.TRUE.equals(dto.getActivar())){
            activarMilitante(militanteId);
        }
        militanteRepository.save(m);
    }
}