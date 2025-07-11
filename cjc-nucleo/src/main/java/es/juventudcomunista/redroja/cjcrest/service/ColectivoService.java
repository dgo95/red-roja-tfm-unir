package es.juventudcomunista.redroja.cjcrest.service;

import es.juventudcomunista.redroja.cjcrest.entity.*;
import es.juventudcomunista.redroja.cjcrest.dto.*;
import es.juventudcomunista.redroja.cjcrest.enums.ChangeType;
import es.juventudcomunista.redroja.cjcrest.keycloak.dto.service.KeycloakAdminService;
import es.juventudcomunista.redroja.cjcrest.repository.*;
import es.juventudcomunista.redroja.cjcrest.mapper.*;
import es.juventudcomunista.redroja.cjcrest.util.FechaUtil;
import es.juventudcomunista.redroja.cjcrest.web.dto.InicializaColectivoDTO;
import es.juventudcomunista.redroja.cjcrest.web.dto.ResponsabilidadDTO;
import es.juventudcomunista.redroja.cjcrest.web.dto.ResponsableDTO;
import es.juventudcomunista.redroja.cjcrest.web.dto.ReunionDTO;
import es.juventudcomunista.redroja.cjcrest.entity.vistas.CensoGeneral;
import es.juventudcomunista.redroja.cjcrest.exception.MilitanteNotFoundException;
import es.juventudcomunista.redroja.cjcrest.web.input.ColectivoInputDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import es.juventudcomunista.redroja.cjccommonutils.dto.MilitanteRolDTO;
import es.juventudcomunista.redroja.cjccommonutils.email.EmailDetails;
import es.juventudcomunista.redroja.cjccommonutils.enums.NombreMicro;
import es.juventudcomunista.redroja.cjccommonutils.enums.Rol;
import es.juventudcomunista.redroja.cjccommonutils.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ColectivoService {
    

    final ColectivoRepository colectivoRepository;
    private final ComiteRepository comiteRepository;
    final MilitanteRepository militanteRepository;
    final ReunionRepository reunionRepository;
    final InvitadoRepository invitadoRepository;
    final EmailSenderService emailSenderService;
    final CensoGeneralRepository censoGeneralRepository;
    final CensoLaboralSindicalRepository censoLaboralSindicalRepository;
    private final CensoEstudiantilRepository censoEstudiantilRepository;
    final InventarioRepository inventarioRepository;
    private final MilitanteRolComiteRepository militanteRolComiteRepository;
    private final RestTemplate restTemplate;
    private final InventarioHistorialRepository inventarioHistorialRepository;
    private final MaterialInventarioRepository materialInventarioRepository;
    private final MilitanteHabilidadRepository militanteHabilidadRepository;
    private final HabilidadRepository habilidadRepository;
    private final ContactoRepository contactoRepository;
    private final DireccionService direccionService;
    private final KeycloakAdminService kcAdmin;


    @Value("${plantilla.nuevaReunion}")
    private String plantillaNuevaReunion;
    
    @Value("${plantilla.editaReunion}")
    private String plantillaEditaReunion;


    public InicializaColectivoDTO inicializaColectivoDe(int id){
        ComiteBase c = colectivoRepository.findById(id).orElseThrow();
        List<Militante> militantes = obtenerMilitantes(c);
        Set<MilitanteRolComite> responsabilidades = c.getResponsabilidades();

        // Carga datos reunion
        Map<String, Object> reunion = cargaDatosReunion(c);

        return InicializaColectivoDTO.builder()
                .responsabilidades(convertirAResponsabilidadDTO(responsabilidades))
                .militantes(mapearAListaResponsableDTO(militantes))
                .reunion(reunion)
                .sede(c.getSede())
                .nombre(c.getNombre())
                .build();
    }

    private Map<String, Object> cargaDatosReunion(ComiteBase c) {
        Map<String, Object> mapa = new HashMap<>();
        Pageable topDos = PageRequest.of(0, 2, Sort.by("fechaInicio").descending());
        List<Reunion> reunionesRecientes = reunionRepository.findByComiteBaseOrderByFechaInicioDesc(c, topDos);
        if (reunionesRecientes.isEmpty()) {
            mapa.put("fechaReunionPasada", "");
            mapa.put("datos", null);
            return mapa;
        }

        if (reunionesRecientes.get(0).isTerminada()) {
            mapa.put("fechaReunionPasada",
                    FechaUtil.formatearFecha(reunionesRecientes.get(0).getFechaInicio(), "dd/MM/yyyy HH:mm"));
            mapa.put("datos", null);
        } else {
            var fechaP = reunionesRecientes.size() == 1 ? ""
                    : FechaUtil.formatearFecha(reunionesRecientes.get(1).getFechaInicio(), "dd/MM/yyyy HH:mm");
            mapa.put("fechaReunionPasada", fechaP);
            reunionesRecientes.get(0).getPuntos().stream().peek(p -> {
                log.debug("Punto: " + p.toString());
                // Esto asegura que map devuelve el punto procesado, en caso de que quieras seguir usando el stream.
            }).collect(Collectors.toList());
            log.debug("Hay este numero de puntos: "+reunionesRecientes.get(0).getPuntos().size());// Suponiendo que quieras recoger los resultados en una lista, aunque esto puede variar según tu necesidad.

            mapa.put("datos", ReunionMapper.INSTANCE.toDTO(reunionesRecientes.get(0)));
        }

        return mapa;
    }

    private List<ResponsabilidadDTO> convertirAResponsabilidadDTO(Set<MilitanteRolComite> militanteRoles) {
        return militanteRoles.stream()
                .map(militanteRol -> ResponsabilidadDTO.builder()
                        .clave(militanteRol.getRol().toString())
                        .valor(militanteRol.getMilitante() != null ? militanteRol.getMilitante().getMilitanteId() : "0")
                        .build())
                .collect(Collectors.toList());
    }

    private List<ResponsableDTO> mapearAListaResponsableDTO(List<Militante> militantes) {
        return militantes.stream()
                .map(militante -> {
                    ResponsableDTO dto = new ResponsableDTO();
                    dto.setId(militante.getMilitanteId());
                    dto.setNombre(militante.getNombre() + " " + militante.getApellido());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private List<Militante> obtenerMilitantes(ComiteBase c) {
        return militanteRepository.findByComitesBaseAndPremilitanteFalse(c);
    }

    @Transactional
    public void actualizarResponsabilidadesDe(int id, List<ResponsabilidadDTO> responsabilidades)
            throws MilitanteNotFoundException {
        ComiteBase c = colectivoRepository.findById(id).orElseThrow();

        List<Militante> militantes = obtenerMilitantes(c);
        List<MilitanteRolComite> mrList = new ArrayList<>();
        for (ResponsabilidadDTO r : responsabilidades) {
            if(r.getValor().isBlank()){
                continue;
            }
            if (militantes.stream().noneMatch(m -> m.getMilitanteId().equals(r.getValor()))) {
                throw new MilitanteNotFoundException("Militante con ID " + r.getValor() + " no pertenece al ComiteBase con ID " + id);
            }
            Rol rol = Rol.valueOf(r.getClave());
            MilitanteRolComite mr = militanteRolComiteRepository.findByComiteBaseAndRol(c, rol).orElseGet(() -> {
                MilitanteRolComite mr1 = new MilitanteRolComite();
                mr1.setComiteBase(c);
                mr1.setRol(rol);
                return mr1;
            });

            // Establece el mr.setMilitante() con el militante de la lista que tenga el
            // mismo Id que r.getValor(), o null si no se encuentra
            Militante militante = militantes.stream()
                    .filter(m -> m.getMilitanteId().equals(r.getValor()))
                    .findFirst()
                    .orElse(null);
            mr.setMilitante(militante);

            mr.setRol(rol);
            mrList.add(mr);
        }
        militanteRolComiteRepository.saveAll(mrList);
        colectivoRepository.save(c);

        // --------------------   SINCRONIZAR KC  --------------------
        Map<Rol, String> mapa = mrList.stream()
                .collect(Collectors.toMap(MilitanteRolComite::getRol,
                        mr -> mr.getMilitante().getMilitanteId()));

        try {
            kcAdmin.syncRoles(c.getFullPath(), mapa);      // ← ❶  llamada real
        } catch (Exception ex) {
            log.error("Fallo sincronizando roles en Keycloak", ex);
            throw new RuntimeException("No se pudo actualizar Keycloak", ex); // ❷  ⇒ rollback
        }
    }

    private void recogerIds(Comite comite, Set<Integer> comitesIds, Set<Integer> comitesBaseIds) {
        comitesIds.add(comite.getId());

        comite.getSubComitesBase().forEach(subComiteBase -> comitesBaseIds.add(subComiteBase.getId()));

        comite.getSubComites().forEach(subComite -> {
            comitesIds.add(subComite.getId());
            recogerIds(subComite, comitesIds, comitesBaseIds);
        });
    }

    public void convocaReunion(Integer id, ReunionDTO dto) throws MilitanteNotFoundException {
        // 1. Validar que el comité existe
        ComiteBase comiteBase = colectivoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el comité con id: " + id));

        // 2. Crear la entidad Reunion y asignar valores desde el DTO
        Reunion reunion = new Reunion();
        reunion.setDireccion(dto.getDireccion());
        reunion.setDuracion(Duration.ofHours(dto.getDuracion())); // Duración como duración en horas
        reunion.setFechaInicio(dto.getFecha().atZone(ZoneId.systemDefault()).toLocalDateTime());
        reunion.setAptaPremilitantes(dto.isPremilitantes());
        reunion.setComiteBase(comiteBase);

        // 3. Convertir los puntos del DTO en entidades y asociarlos a la reunión
        List<Punto> puntos = dto.getPuntos() != null
                ? PuntoMapper.INSTANCE.toEntities(dto.getPuntos())
                : new ArrayList<>();

        puntos.forEach(punto -> {
            punto.setReunion(reunion);
            punto.setId(null);
            punto.getSubpuntos().forEach(subpunto -> subpunto.setId(null));
        });
        reunion.setPuntos(puntos);

        // 4. Procesar los invitados de la reunión
        List<Invitado> invitados = dto.getInvitados() != null
                ? dto.getInvitados().stream().map(invitadoDto -> {
            Invitado invitado = new Invitado();
            invitado.setNombre(invitadoDto.getNombre());
            invitado.setEmail(invitadoDto.getEmail());
            invitado.setEsMilitante(invitadoDto.isEsMilitante());
            invitado.setNumeroCarnet(invitadoDto.getNumeroCarnet());
            invitado.setReunion(reunion);

            // Validar el número de carnet para militantes
            if (invitado.isEsMilitante()) {
                // Buscar militante por algún identificador (ejemplo: email o número de carnet)
                var militante = militanteRepository.findByNumeroCarnet(invitado.getNumeroCarnet())
                        .orElseThrow(() -> new MilitanteNotFoundException(
                                "No se encontró el militante con email: " + invitado.getEmail()));
            }

            return invitado;
        }).toList()
                : new ArrayList<>();

        reunion.setInvitados(invitados);

        // 5. Guardar la entidad de la reunión
        reunionRepository.save(reunion);

        mandaEmailConvocaReunion(comiteBase, reunion, null);
    }

    public Integer editaReunion(Integer id, ReunionDTO reunionDTO) {
        log.debug("Iniciando la edición de la reunión con ID: {}", id);
        var r = reunionRepository.findById(id).orElseThrow();
        log.debug("Reunión encontrada: {}", r);
        var c = colectivoRepository.findByReunionId(r.getId()).orElseThrow();
        log.debug("Colectivo asociado a la reunión encontrado: {}", c);
        List<String> camposModificados = new ArrayList<>();

        if (reunionDTO.getDireccion() != null) {
            r.setDireccion(reunionDTO.getDireccion());
            camposModificados.add("direccion");
            log.debug("Dirección actualizada a: {}", reunionDTO.getDireccion());
        }
        if (reunionDTO.getDuracion() != null) {
            r.setDuracion(Duration.ofHours(reunionDTO.getDuracion()));
            camposModificados.add("duracion");
            log.debug("Duración actualizada a: {}", reunionDTO.getDuracion());
        }
        if (reunionDTO.getFecha() != null) {
            r.setFechaInicio(reunionDTO.getFecha().atZone(ZoneId.systemDefault()).toLocalDateTime());
            camposModificados.add("fecha");
            log.debug("Fecha actualizada a: {}", reunionDTO.getFecha());
        }
        if (reunionDTO.getPuntos() != null && !reunionDTO.getPuntos().isEmpty()) {
            camposModificados.add("ordenDelDia");
            log.debug("Orden del día actualizado");
        }

        List<Invitado> invitadosNuevos = null;
        if (reunionDTO.getInvitados() != null && !reunionDTO.getInvitados().isEmpty()) {
            log.debug("Procesando invitados para la reunión");
            Set<String> emailsExistentes = r.getInvitados().stream()
                    .map(invitado -> {
                        if (invitado.isEsMilitante()) {
                            Militante militante = militanteRepository.findByNumeroCarnet(invitado.getNumeroCarnet()).orElseThrow();
                            return militante.getEmail();
                        } else {
                            return invitado.getEmail();
                        }
                    })
                    .collect(Collectors.toSet());
            log.debug("Emails existentes recolectados: {}", emailsExistentes);

            
            invitadosNuevos = reunionDTO.getInvitados().stream()
                                                        .filter(invitadoDTO -> !emailsExistentes.contains(invitadoDTO.getEmail()))
                                                        .collect(Collectors.toList());
            log.debug("Filtrados nuevos invitados: {}", invitadosNuevos);

            invitadosNuevos = procesaInvitados(invitadosNuevos, r);

            invitadosNuevos.forEach(invitado -> {
                if (invitado.isEsMilitante()) {
                    var m = militanteRepository.findByNumeroCarnet(invitado.getNumeroCarnet()).orElseThrow();
                    enviarEmail("email-edita-reunion", m, r, c,null);
                    log.debug("Email enviado a militante: {}", m);
                } else {
                    // Llama al método enviarEmail para los que no son militantes
                    enviarEmail("email-edita-reunion", invitado, r, c,null);
                    log.debug("Email enviado a no militante: {}", invitado);
                }
            });
        }
     // Aquí se enviaría el email con los camposModificados como parte del contenido
        mandaEmailConvocaReunion(c,r, camposModificados);
        
        
        if(reunionDTO.isPremilitantes() && !r.isAptaPremilitantes()) {
            var premilitantes = militanteRepository.findByComitesBaseAndPremilitanteTrue(c);
            premilitantes.forEach(pre -> {
                enviarEmail("email-edita-reunion", pre, r, c, null);
                log.debug("Email enviado a premilitante: {}", pre);
            });
        }
        if (invitadosNuevos != null && !invitadosNuevos.isEmpty()) {
            r.getInvitados().addAll(invitadosNuevos);
            log.debug("Invitados nuevos añadidos a la reunión");
        }
        reunionRepository.save(r);
        log.debug("Reunión guardada con éxito: {}", r);
        return militanteRepository.findByComitesBase(c).get(0).getId();
    }

    private void mandaEmailConvocaReunion(ComiteBase c, Reunion r, List<String> camposModificados) {

        
        // Obtener lista de militantes
        List<Militante> militantes = r.isAptaPremilitantes() ? militanteRepository.findByComitesBase(c):militanteRepository.findByComitesBaseAndPremilitanteFalse(c);

        // Copiar lista de invitados y filtrar los que son militantes
        List<Invitado> invitadosMilitantes = new ArrayList<>(r.getInvitados());
        invitadosMilitantes.removeIf(invitado -> !invitado.isEsMilitante());

        // Buscar y eliminar militantes invitados de la lista original
        invitadosMilitantes.forEach(invitado -> {
            Optional<Militante> militanteInvitado = militanteRepository.findByNumeroCarnet(invitado.getNumeroCarnet());
            militanteInvitado.ifPresent(militantes::add);
        });

        // Enviar emails a militantes
        militantes.forEach(militante -> enviarEmail("email-convocatoria", militante, r, c,camposModificados));

        // Enviar emails a los invitados restantes
        r.getInvitados().stream()
                .filter(invitado -> !invitado.isEsMilitante())
                .forEach(invitado -> enviarEmail("email-convocatoria", invitado, r, c,camposModificados));
    }

    private void enviarEmail(String topic, Militante m, Reunion r, ComiteBase c, List<String> camposModificados) {
        EmailDetails emailDetails = crearEmailDetails(m.getEmail(), r, c,m.getNombre(),camposModificados);
        if(emailDetails!=null) emailSenderService.sendEmail(topic, emailDetails);
    }

    private void enviarEmail(String topic, Invitado i, Reunion r, ComiteBase c, List<String> camposModificados) {
        EmailDetails emailDetails = crearEmailDetails(i.getEmail(), r, c,i.getNombre(),camposModificados);
        if(emailDetails!=null) emailSenderService.sendEmail(topic, emailDetails);
    }

    private EmailDetails crearEmailDetails(String email, Reunion r, ComiteBase c,String nombre, List<String> camposModificados) {

        var editar = camposModificados != null;
        if(editar && camposModificados.isEmpty()) return null;
        var plantilla = editar ? plantillaEditaReunion : plantillaNuevaReunion;
        
        Map<String, String> mapa = new HashMap<>();
        // Formato para la fecha y la hora
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");

        String ordenDelDiaEscapado = r.getPuntos().toString();
        // Rellena el mapa con los datos
        mapa.put("colectivo", c.getNombre());
        mapa.put("nombre", nombre);
        mapa.put("fecha", obtieneDiaSemana(r.getFechaInicio().getDayOfWeek()) + " " +
                r.getFechaInicio().format(formatoFecha));
        mapa.put("hora", r.getFechaInicio().format(formatoHora));
        mapa.put("duracion", r.getDuracion().toHours() + "h");
        mapa.put("ubicacion", r.getDireccion());
        mapa.put("ordenDelDia", ordenDelDiaEscapado.replace("\n", "<br/>"));
        // Creación del asunto del correo
        String asunto = "[" + c.getNombre() + "] Convocatoria reunión " + mapa.get("fecha");
        if(editar) {
            mapa.put("camposModificados", String.join(", ", camposModificados));
            asunto = asunto.replace("Convocatoria", "Modificación convocatoria");
        }
        
        return EmailDetails.builder()
                .to(email)
                .subject(asunto)
                .plantilla(plantilla)
                .variables(mapa)
                .build();
    }

    private String obtieneDiaSemana(DayOfWeek dayOfWeek) {
        String aux = dayOfWeek.getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
        return aux.substring(0, 1).toUpperCase().concat(aux.substring(1).toLowerCase());
    }

    public List<Invitado> procesaInvitados(List<Invitado> invitados, Reunion reunion) {
        // Valida si la lista de invitados o la reunion son null y maneja el caso adecuadamente
        if (invitados == null || reunion == null) {
            // Puedes decidir cómo manejar este caso: lanzar una excepción, retornar una lista vacía, etc.
            // Aquí optamos por retornar una lista vacía para evitar la propagación de null.
            return Collections.emptyList();
        }

        return invitados.stream()
                .filter(Objects::nonNull) // Filtra los elementos null de la lista
                .map(invitado -> {
                    invitado.setReunion(reunion); // Asigna la reunión al invitado
                    return Optional.of(invitadoRepository.save(invitado)); // Guarda cada invitado en la base de datos y maneja el null con Optional
                })
                .filter(Optional::isPresent) // Filtra los posibles Optionals vacíos
                .map(Optional::get) // Extrae el valor de los Optionals
                .collect(Collectors.toList()); // Recolecta los resultados en una lista
    }

    public Page<CensoGeneralDTO> censoGeneral(Integer id, Pageable pageable) {
        var comiteBase = colectivoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comité base no encontrado con id: " + id));

        List<Integer> ids = militanteRepository.findIdsByComitesBase(comiteBase);

        Page<CensoGeneral> censo = censoGeneralRepository.findByIdIn(ids, pageable);

        // Map entities to DTOs
        Page<CensoGeneralDTO> censoDTOs = censo.map(CensoGeneralMapper.INSTANCE::toDTO);

        // Rellenar habilidades
        censoDTOs = censoDTOs.map(this::getHabilidades);

        // Rellenar habilidades
        censoDTOs = censoDTOs.map(this::tieneFichaMovilidad);


        return censoDTOs;
    }

    private CensoGeneralDTO tieneFichaMovilidad(CensoGeneralDTO censoGeneralDTO) {
        Militante militante = militanteRepository.findByMilitanteId(censoGeneralDTO.getId())
                .orElseThrow(() -> new NoSuchElementException("Militante no encontrado con ID: " + censoGeneralDTO.getId()));

        boolean tieneFichaMovilidad = militante.getFichaMovilidad() != null;
        censoGeneralDTO.setFichaMovilidad(tieneFichaMovilidad);

        return censoGeneralDTO;
    }


    private CensoGeneralDTO getHabilidades(CensoGeneralDTO entity) {
        var m = militanteRepository.findByMilitanteId(entity.getId()).orElseThrow();
        List<MilitanteHabilidad> lista = militanteHabilidadRepository.findByMilitante(m);
        var habilidades = habilidadRepository.findAll();
        Map<String,String> mapa = new HashMap<>();

        for (Habilidad habilidad : habilidades) {
            String key = toCamelCase(habilidad.getNombre());
            String value = "-";
            mapa.put(key, value);
        }

        for (MilitanteHabilidad habilidad : lista) {
            String key = toCamelCase(habilidad.getHabilidad().getNombre());
            String value = habilidad.getDescripcion();
            mapa.put(key, value);
        }

        entity.setHabilidades(mapa);
        return entity;
    }

    private String toCamelCase(String input) {
        return Stream.of(input.split("\\s+"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining());
    }

    public Page<CensoLaboralSindicalDTO> censoLaboral(Integer id, Pageable pageable) {
        var comiteBase = colectivoRepository.findById(id).orElseThrow();
        List<Integer> ids = militanteRepository.findIdsByComitesBase(comiteBase);

        // Aquí, usamos un método que soporte Pageable
        var censo = censoLaboralSindicalRepository.findByIdIn(ids, pageable);

        // Map entities to DTOs

        return censo.map(CensoLaboralSindicalMapper.INSTANCE::toDTO);
    }

    public Page<CensoEstudiantilDTO> censoEstudiantil(Integer id, Pageable pageable) {
        var comiteBase = colectivoRepository.findById(id).orElseThrow();
        List<Integer> ids = militanteRepository.findIdsByComitesBase(comiteBase);

        // Aquí, usamos un método que soporte Pageable
        var censo = censoEstudiantilRepository.findByIdIn(ids, pageable);

        // Map entities to DTOs

        return censo.map(CensoEstudiantilMapper.INSTANCE::toDTO);
    }

    public ComiteBaseDTO crear(ColectivoInputDTO colectivoDTO) {

        Comite comite = comiteRepository.findById(colectivoDTO.getComiteDependienteId()).orElseThrow();//TODO el comite superior no existe

        Inventario inventario = new Inventario();
        inventario = inventarioRepository.save(inventario);

        var colectivo = ComiteBase.builder()
                .comiteDependiente(comite)
                .email(colectivoDTO.getEmail())
                .sede(colectivoDTO.getSede())
                .nombre(colectivoDTO.getNombre())
                .organizacion(colectivoDTO.getOrganizacion())
                .inventario(inventario)
                .build();
        colectivoRepository.save(colectivo);

        colectivoDTO.getMilitantesIds().forEach(militanteId -> cambiarDeComiteBaseAMilitante(colectivo.getId(),militanteId));

        var respo = colectivoDTO.getResponsabilidadIds();
        List<ResponsabilidadDTO> lista = respo.stream()
                .map(this::crearMilitanteRol)
                .collect(Collectors.toList());
        actualizarResponsabilidadesDe(colectivo.getId(),lista);


        return ComiteBaseMapper.INSTANCE.toDto(colectivo);
    }

    public void cambiarDeComiteBaseAMilitante(Integer id, String militanteId) {
        Militante m = militanteRepository.findByMilitanteId(militanteId)
                .orElseThrow(() -> new MilitanteNotFoundException("No existe el militante con militanteId: " + militanteId));
        ComiteBase destino = colectivoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe el comite base con id: " + id));

        // Si m.getComitesBase() es igual a destino, finaliza
        if (m.getComitesBase().stream().anyMatch( c -> c.getId().equals(id))) {
            return;
        }

        // Si militanteRolRepository.findByMilitante devuelve una lista no vacía
        List<MilitanteRolComite> roles = militanteRolComiteRepository.findByMilitante(m);
        for (MilitanteRolComite rol : roles) {
            if (rol.getComiteBase() != null) {
                throw new IllegalStateException("El militante tiene roles asignados a un comite base y no puede cambiar de comité base.");
            } else if (rol.getComite() != null) {
                // Verificar recursivamente los subcomités
                if (isDestinoInSubComites(rol.getComite(), destino)) {
                    return;
                }
            }
        }

        // Buscar el ComiteBase con la misma organización y eliminarlo del HashSet
        Optional<ComiteBase> comiteBaseActual = m.getComitesBase().stream()
                .filter(cb -> cb.getOrganizacion().equals(destino.getOrganizacion()))
                .findFirst();

        comiteBaseActual.ifPresent(m.getComitesBase()::remove);

        // Añadir el nuevo ComiteBase destino al HashSet
        m.getComitesBase().add(destino);

        // Guardar los cambios en el militante
        militanteRepository.save(m);
    }

    private boolean isDestinoInSubComites(Comite comite, ComiteBase destino) {
        // Verificar si el destino está en los subComitesBase del comité actual
        if (comite.getSubComitesBase().contains(destino)) {
            return true;
        }

        // Verificar recursivamente en los subComites
        for (Comite subComite : comite.getSubComites()) {
            if (isDestinoInSubComites(subComite, destino)) {
                return true;
            }
        }

        return false;
    }

    private ResponsabilidadDTO crearMilitanteRol(MilitanteRolDTO militanteRol) {
        return ResponsabilidadDTO.builder()
                .clave(militanteRol.getRol().toString())
                .valor(militanteRol.getMilitanteId())
                .build();
    }

    public ComiteBaseDTO obtenerDtoPorId(Integer id) {
        ComiteBase c = obtenerPorId(id);
        return ComiteBaseMapper.INSTANCE.toDto(c);
    }
    public ComiteBase obtenerPorId(Integer id) {
        ComiteBase c = colectivoRepository.findById(id).orElseThrow( () -> new EntityNotFoundException("No existe el comite base con id: " + id));
        return c;
    }


    public List<ComiteBaseDTO> obtenerTodos() {

        var todos = colectivoRepository.findAll();
        return ComiteBaseMapper.INSTANCE.toDtoList(todos);
    }

    public ComiteBaseDTO actualizar(Integer id, ColectivoInputDTO colectivoDTO) {
        return null;
        //TODO
    }

    public void eliminar(Integer id) {
        //TODO
    }

    public Page<MaterialDTO> getInventario(int id, Pageable pageable) {
        var colectivo = colectivoRepository.findById(id).orElseThrow(() -> new RuntimeException("Colectivo no encontrado"));
        var inventario = colectivo.getInventario();

        // Transformar MaterialInventario a MaterialDTO
        List<MaterialDTO> materialDTOs = inventario.getMateriales().stream().map(this::convertToMaterialDTO).collect(Collectors.toList());

        // Paginación
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), materialDTOs.size());
        Page<MaterialDTO> page = new PageImpl<>(materialDTOs.subList(start, end), pageable, materialDTOs.size());

        return page;
    }

    private MaterialDTO convertToMaterialDTO(MaterialInventario materialInventario) {
        int cantidadTotal = materialInventario.getAsignaciones().stream().mapToInt(AsignacionMaterial::getCantidad).sum();

        // Crear el mapa de responsables
        HashMap<String, Object> responsables = new HashMap<>();
        for (AsignacionMaterial asignacion : materialInventario.getAsignaciones()) {
            String responsableKey = "";
            if (asignacion.getMilitante() != null) {
                responsableKey = asignacion.getMilitante().getNombre() + " " + asignacion.getMilitante().getApellido() + (asignacion.getMilitante().getApellido2() != null && !asignacion.getMilitante().getApellido2().isBlank() ? " " + asignacion.getMilitante().getApellido2() : "");
            } else if (asignacion.getComite() != null) {
                responsableKey = asignacion.getComite().getSede() + " (" + asignacion.getComite().getNombre() + ")";
            } else if (asignacion.getComiteBase() != null) {
                responsableKey = asignacion.getComiteBase().getSede() + " (" + asignacion.getComiteBase().getNombre() + ")";
            }
            HashMap<String, Object> cantidad = new HashMap<>();
            cantidad.put("id", asignacion.getId());
            cantidad.put("cantidad", asignacion.getCantidad());
            responsables.put(responsableKey, cantidad);
        }

        return MaterialDTO.builder()
                .nombre(materialInventario.getNombre())
                .descripcion(materialInventario.getDescription())
                .cantidadTotal(cantidadTotal)
                .responsables(responsables)
                .build();
    }

    public MaterialInventario addNewMaterial(int colectivoId, MaterialInputDTO materialDTO) {
        var colectivo = colectivoRepository.findById(colectivoId).orElseThrow(() -> new RuntimeException("Colectivo no encontrado"));
        var inventario = colectivo.getInventario();

        // Crear nuevo material
        MaterialInventario newMaterial = MaterialInventario.builder()
                .inventario(inventario)
                .nombre(materialDTO.getNombre())
                .description(materialDTO.getDescripcion())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Crear historial inicial
        int cantidadTotal = materialDTO.getResponsables().values().stream().mapToInt(Integer::intValue).sum();

        // Generar la descripción formal
        StringBuilder descripcion = new StringBuilder();
        descripcion.append(materialDTO.getNombre())
                .append(" se han añadido por primera vez. Total:  ")
                .append(cantidadTotal)
                .append(" unidades.");

        // Crear asignaciones iniciales
        List<AsignacionMaterial> asignaciones = materialDTO.getResponsables().entrySet().stream()
                .map(entry -> {
                    AsignacionMaterial asignacion = new AsignacionMaterial();
                    asignacion.setMaterialInventario(newMaterial);
                    asignacion.setCantidad(entry.getValue());

                    String responsableKey = entry.getKey();
                    if (responsableKey.startsWith("M:")) {
                        // Asignar a un militante
                        String militanteId = responsableKey.substring(2);
                        Militante militante = militanteRepository.findByMilitanteId(militanteId).orElseThrow(() -> new RuntimeException("Militante no encontrado"));
                        asignacion.setMilitante(militante);
                    } else if (responsableKey.matches("\\d+")) {
                            // Asignar a un comité
                            int comiteId = Integer.parseInt(responsableKey);
                            Comite comite = comiteRepository.findById(comiteId).orElseThrow(() -> new RuntimeException("Comité no encontrado"));
                            asignacion.setComite(comite);
                    } else{
                            // Asignar al comité base del inventario
                            asignacion.setComiteBase(colectivo);
                    }

                    return asignacion;
                }).collect(Collectors.toList());
        newMaterial.setAsignaciones(asignaciones);

        inventario.getMateriales().add(newMaterial);


        InventarioHistorial historial = InventarioHistorial.builder()
                .materialInventario(newMaterial)
                .changeType(ChangeType.ADDED)
                .cantidad(cantidadTotal)
                .changeDate(LocalDateTime.now())
                .description(descripcion.toString())
                .build();
        newMaterial.setHistorial(List.of(historial));
        materialInventarioRepository.save(newMaterial);
        inventarioHistorialRepository.save(historial);

        return newMaterial;
    }

    public ContactoDTO inicializaContacto(Long contactoId, Integer comiteId) {
        ContactoDTO dto;
        if (contactoId == null) {
            // Si no hay contactoId, creamos un nuevo ContactoDTO vacío
            dto = crearContactoVacio();
        } else {
            // Si hay contactoId, buscamos el contacto, y si no existe, devolvemos un ContactoDTO vacío
            dto = contactoRepository.findById(contactoId)
                    .map(ContactoMapper.INSTANCE::toDTO)
                    .orElseGet(this::crearContactoVacio);  // Si no se encuentra, devolvemos un ContactoDTO vacío
        }
        return completaDirecciones(dto,comiteId);
    }

    private ContactoDTO completaDirecciones(ContactoDTO dto, Integer comiteId) {
        if(dto.getMunicipio() == null){
            var comunidades = direccionService.obtenerTodasLasComunidadesAutonomas();
            var comunidadesDTOS = ComunidadAutonomaMapper.INSTANCE.toDTOs(comunidades);
            dto.setComunidades(comunidadesDTOS);
        }else{
            var municipio = direccionService.getMunicipioById(dto.getMunicipio());
            var municipios = direccionService.obtenerMunicipiosPorProvinciaId(municipio.getProvincia().getId());
            var provincias = direccionService.obtenerProvinciasPorComunidadId(municipio.getProvincia().getComunidadAutonoma().getId());
            var comunidades = direccionService.obtenerTodasLasComunidadesAutonomas();
            var comunidadesDTOS = ComunidadAutonomaMapper.INSTANCE.toDTOs(comunidades);
            dto.setComunidades(comunidadesDTOS);
            dto.setProvincias(provincias);
            dto.setMunicipios(municipios);
            dto.setMunicipio(municipio.getId());
            dto.setProvincia(municipio.getProvincia().getId());
            dto.setComunidad(municipio.getProvincia().getComunidadAutonoma().getId());
        }
        var c = obtenerPorId(comiteId);
        List<Militante> militantes = obtenerMilitantes(c);
        dto.setResponsables(mapearAListaResponsableDTO(militantes));
        return dto;
    }

    private ContactoDTO crearContactoVacio() {
        // Crear un nuevo DTO vacío o con valores predeterminados
        return ContactoDTO.builder()
                .nombre("")
                .fechaNacimiento(null)  // Puedes poner LocalDate.now() si lo prefieres
                .estudiante(false)
                .trabajador(false)
                .telefono("")
                .email("")
                .municipio(null)
                .situacionOrigen("")
                .estadoActual("")
                .militanteId(null)
                .proximaTarea("")
                .build();
    }
}
