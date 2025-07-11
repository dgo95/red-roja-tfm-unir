package es.juventudcomunista.redroja.cjcrest.mapper;

import es.juventudcomunista.redroja.cjcrest.entity.ComunidadAutonoma;
import es.juventudcomunista.redroja.cjcrest.web.dto.ComunidadAutonomaDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ComunidadAutonomaMapper {
    ComunidadAutonomaMapper INSTANCE = Mappers.getMapper(ComunidadAutonomaMapper.class);

    ComunidadAutonoma toEntity(ComunidadAutonomaDTO dto);

    ComunidadAutonomaDTO toDTO(ComunidadAutonoma entity);

    List<ComunidadAutonoma> toEntities(List<ComunidadAutonomaDTO> dtos);

    List<ComunidadAutonomaDTO> toDTOs(List<ComunidadAutonoma> entities);
}

