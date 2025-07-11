package es.juventudcomunista.redroja.cjcrest.service;

import es.juventudcomunista.redroja.cjcrest.dto.CantidadMaterialDTO;
import es.juventudcomunista.redroja.cjcrest.dto.DatosMaterialDTO;
import es.juventudcomunista.redroja.cjcrest.dto.InventarioHistorialDTO;
import es.juventudcomunista.redroja.cjcrest.entity.AsignacionMaterial;
import es.juventudcomunista.redroja.cjcrest.entity.InventarioHistorial;
import es.juventudcomunista.redroja.cjcrest.entity.MaterialInventario;
import es.juventudcomunista.redroja.cjcrest.enums.ChangeType;
import es.juventudcomunista.redroja.cjcrest.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventarioService {
    private final AsignacionMaterialRepository asignacionMaterialRepository;
    private final InventarioRepository inventarioRepository;
    private final InventarioHistorialRepository inventarioHistorialRepository;
    private final MilitanteRepository militanteRepository;
    private final ComiteRepository comiteRepository;
    private final ColectivoRepository comiteBaseRepository;
    private final MaterialInventarioRepository materialInventarioRepository;


    @Transactional
    public void updateMaterialCantidad(int asignacionId, CantidadMaterialDTO cantidadMaterialDTO) {
        var asignacion = asignacionMaterialRepository.findById(asignacionId)
                .orElseThrow(() -> new RuntimeException("Asignación no encontrada"));

        validarCantidadYTipo(cantidadMaterialDTO);

        var materialInventario = asignacion.getMaterialInventario();

        // Calcular la cantidad total actual del material
        int cantidadTotalAntes = materialInventario.getAsignaciones().stream()
                .mapToInt(AsignacionMaterial::getCantidad)
                .sum();

        // Calcular la nueva cantidad para la asignación específica
        int nuevaCantidadAsignacion = asignacion.getCantidad() + cantidadMaterialDTO.getCantidad();
        log.debug("Nueva cantidad calculada para la asignación: {}", nuevaCantidadAsignacion);

        if (nuevaCantidadAsignacion  < 0) {
            throw new RuntimeException("La cantidad no puede ser negativa");
        } else if (nuevaCantidadAsignacion  == 0) {
            log.debug("Eliminando asignación con ID: {}", asignacionId);
            eliminarReferencias(asignacion);
            materialInventario.getAsignaciones().remove(asignacion);
            asignacionMaterialRepository.delete(asignacion);
            log.debug("Asignación eliminada");
        } else {
            asignacion.setCantidad(nuevaCantidadAsignacion );
            asignacionMaterialRepository.save(asignacion);
            log.debug("Asignación actualizada con nueva cantidad: {}", nuevaCantidadAsignacion );
        }

        // Calcular la nueva cantidad total del material
        int cantidadTotalDespues = materialInventario.getAsignaciones().stream()
                .mapToInt(AsignacionMaterial::getCantidad)
                .sum();

        // Generar la descripción formal
        StringBuilder descripcion = new StringBuilder();
        descripcion.append(materialInventario.getNombre())
                .append(" ahora tiene una cantidad total de ")
                .append(cantidadTotalDespues)
                .append(" unidades. (Anteriormente: ")
                .append(cantidadTotalAntes)
                .append(" unidades).");
        if (nuevaCantidadAsignacion == 0) {
            // Determinar el nombre dependiendo de que campo esté informado
            String nombre = getResponsable(asignacion);
            descripcion
                    .append(nombre)
                    .append("' se ha quedado sin unidades, asignación eliminada.");
        }

        var inventario = materialInventario.getInventario();

        if(materialInventario.getAsignaciones().isEmpty()){
            inventario.getMateriales().remove(materialInventario);
            materialInventarioRepository.deleteById(materialInventario.getId());
            descripcion.append("\nEl material '").append(materialInventario.getNombre()).append("' se ha agotado.");
            materialInventario = null;
        }
        guardarHistorial(materialInventario, cantidadMaterialDTO, descripcion.toString());
        inventarioRepository.save(inventario);

    }

    private static String getResponsable(AsignacionMaterial asignacion) {
        String nombre;
        if (asignacion.getMilitante() != null) {
            var militante = asignacion.getMilitante();
            nombre = militante.getNombre() + " " + militante.getApellido();
        } else if (asignacion.getComiteBase() != null) {
            var comiteBase = asignacion.getComiteBase();
            nombre = comiteBase.getSede() + " (" + comiteBase.getNombre() + ")";
        } else {
            var comite = asignacion.getComite();
            nombre = comite.getSede() + " (" + comite.getNombre() + ")";
        }
        return nombre;
    }

    private void eliminarReferencias(AsignacionMaterial asignacion) {
        if (asignacion.getMilitante() != null) {
            var militante = asignacion.getMilitante();
            militante.getMateriales().remove(asignacion);
            militanteRepository.save(militante);
        }
        if (asignacion.getComite() != null) {
            var comite = asignacion.getComite();
            comite.getMateriales().remove(asignacion);
            comiteRepository.save(comite);
        }
        if (asignacion.getComiteBase() != null) {
            var comiteBase = asignacion.getComiteBase();
            comiteBase.getMateriales().remove(asignacion);
            comiteBaseRepository.save(comiteBase);
        }
    }

    private void guardarHistorial(MaterialInventario materialInventario, CantidadMaterialDTO cantidadMaterialDTO, String d) {
        InventarioHistorial historial = InventarioHistorial.builder()
                .materialInventario(materialInventario)
                .changeType(cantidadMaterialDTO.getTipo())
                .cantidad(cantidadMaterialDTO.getCantidad())
                .changeDate(LocalDateTime.now())
                .description(d)
                .build();

        inventarioHistorialRepository.save(historial);
    }

    private void validarCantidadYTipo(CantidadMaterialDTO cantidadMaterialDTO) {
        if (cantidadMaterialDTO.getCantidad() == 0) {
            throw new RuntimeException("La cantidad no puede ser cero");
        }

        if (cantidadMaterialDTO.getTipo() == ChangeType.ADDED && cantidadMaterialDTO.getCantidad() < 0) {
            throw new RuntimeException("La cantidad debe ser positiva para el tipo ADD");
        }

        if (cantidadMaterialDTO.getTipo() != ChangeType.ADDED && cantidadMaterialDTO.getCantidad() > 0) {
            throw new RuntimeException("La cantidad debe ser negativa para el tipo " + cantidadMaterialDTO.getTipo());
        }
    }

    public void updateMaterialDatos(int materialId, DatosMaterialDTO datos) {
        var material = materialInventarioRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Material no encontrado"));

        String descripcionCambio = String.format("Nombre cambiado de '%s' a '%s', Descripción cambiada de '%s' a '%s'",
                material.getNombre(), datos.getNombre(), material.getDescription(), datos.getDescripcion());

        material.setNombre(datos.getNombre());
        material.setDescription(datos.getDescripcion());

        InventarioHistorial historial = InventarioHistorial.builder()
                .materialInventario(material)
                .changeType(ChangeType.EDITED)
                .description(descripcionCambio)
                .cantidad(0)
                .changeDate(LocalDateTime.now())
                .build();
        material.getHistorial().add(historial);
        materialInventarioRepository.save(material);
    }

    public Page<InventarioHistorialDTO> getHistorial(int inventarioId, Pageable page, List<ChangeType> changeTypes, List<String> materialInventarioNombres) {
        // Obtener el inventario por su ID, lanzar excepción si no se encuentra
        var inventario = inventarioRepository.findById(inventarioId).orElseThrow(() -> new RuntimeException("Inventario no encontrado"));

        // Obtener la lista de materiales del inventario
        var listaMateriales = inventario.getMateriales();

        // Inicializar una lista para guardar todo el historial
        List<InventarioHistorial> historial = new ArrayList<>();

        // Recorrer la lista de materiales y agregar su historial a la lista principal
        for (MaterialInventario material : listaMateriales) {
            historial.addAll(material.getHistorial());
        }

        // Filtrar por ChangeType si se proporciona
        if (changeTypes != null && !changeTypes.isEmpty()) {
            historial = historial.stream()
                    .filter(h -> changeTypes.contains(h.getChangeType()))
                    .collect(Collectors.toList());
        }

        // Filtrar por nombre del MaterialInventario si se proporciona
        if (materialInventarioNombres != null && !materialInventarioNombres.isEmpty()) {
            historial = historial.stream()
                    .filter(h -> materialInventarioNombres.contains(h.getMaterialInventario().getNombre()))
                    .collect(Collectors.toList());
        }

        // Ordenar el historial por fecha de cambio en orden descendente
        historial.sort(Comparator.comparing(InventarioHistorial::getChangeDate).reversed());

        // Transformar la lista de historial en una lista de DTOs
        List<InventarioHistorialDTO> historialDTOs = historial.stream()
                .map(h -> InventarioHistorialDTO.builder()
                        .materialInventario(h.getMaterialInventario().getNombre())
                        .changeType(h.getChangeType())
                        .variacion(h.getCantidad())
                        .changeDate(h.getChangeDate())
                        .description(h.getDescription())
                        .build())
                .collect(Collectors.toList());

        // Crear la página de resultados
        int start = (int) page.getOffset();
        int end = Math.min((start + page.getPageSize()), historialDTOs.size());
        Page<InventarioHistorialDTO> pageHistorialDTO = new PageImpl<>(historialDTOs.subList(start, end), page, historialDTOs.size());

        // Devolver la página de resultados
        return pageHistorialDTO;
    }
}
