package es.juventudcomunista.redroja.cjcdocumentos.mapper;

import es.juventudcomunista.redroja.cjcdocumentos.dto.ActualizarDocumentoDTO;
import es.juventudcomunista.redroja.cjcdocumentos.entity.Documento;
import es.juventudcomunista.redroja.cjcdocumentos.dto.DocumentoRecibidoDTO;
import org.mapstruct.*;
import java.time.*;

/**
 * Convierte entre la entidad Documento y su DTO.
 */
@Mapper(componentModel = "spring")   // para que Spring pueda inyectarlo
public interface DocumentoMapper {

    /* ---------- Entidad -> DTO ---------- */
    @Mapping(source = "nombreOriginal", target = "titulo")
    @Mapping(source = "uuid",             target = "id")
    @Mapping(source = "fechaSubida",    target = "fecha",
            qualifiedByName = "instantToLocalDate")
    DocumentoRecibidoDTO toDTO(Documento entity);

    /* ---------- DTO -> Entidad ---------- */
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "fecha", target = "fechaSubida",
            qualifiedByName = "localDateToInstant")
    Documento toEntity(DocumentoRecibidoDTO dto);

    /**
     * Mezcla los campos no nulos del DTO sobre la entidad.
     * Elimina la necesidad de hacerlo “a mano” en el servicio.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "titulo", target = "nombreOriginal")
    void updateEntityFromDto(ActualizarDocumentoDTO dto, @MappingTarget Documento entity);

    /* ---------- Conversores auxiliares ---------- */
    @Named("instantToLocalDate")
    default LocalDate instantToLocalDate(Instant value) {
        return value == null ? null : value.atZone(ZoneOffset.UTC).toLocalDate();
    }

    @Named("localDateToInstant")
    default Instant localDateToInstant(LocalDate value) {
        return value == null ? null : value.atStartOfDay(ZoneOffset.UTC).toInstant();
    }
}