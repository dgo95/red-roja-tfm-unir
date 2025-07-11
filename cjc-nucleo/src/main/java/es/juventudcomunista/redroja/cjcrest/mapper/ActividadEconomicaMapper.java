
package es.juventudcomunista.redroja.cjcrest.mapper;

import es.juventudcomunista.redroja.cjcrest.entity.ActividadEconomica;
import es.juventudcomunista.redroja.cjcrest.web.dto.ActividadEconomicaDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
@Mapper
public interface ActividadEconomicaMapper {
    ActividadEconomicaMapper INSTANCE = Mappers.getMapper(ActividadEconomicaMapper.class);

    ActividadEconomica toEntity(ActividadEconomicaDTO dto);

    ActividadEconomicaDTO toDTO(ActividadEconomica entity);

    List<ActividadEconomica> toEntities(List<ActividadEconomicaDTO> dtos);

    List<ActividadEconomicaDTO> toDTOs(List<ActividadEconomica> entities);
}
