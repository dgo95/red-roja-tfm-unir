package es.juventudcomunista.redroja.cjcrest.mapper;

import es.juventudcomunista.redroja.cjcrest.entity.Punto;
import es.juventudcomunista.redroja.cjcrest.web.dto.PuntoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PuntoMapper {
    PuntoMapper INSTANCE = Mappers.getMapper(PuntoMapper.class);

    Punto toEntity(PuntoDTO dto);

    PuntoDTO toDTO(Punto entity);

    List<Punto> toEntities(List<PuntoDTO> dtos);

    List<PuntoDTO> toDTOs(List<Punto> entities);
}
