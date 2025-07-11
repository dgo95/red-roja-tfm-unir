
package es.juventudcomunista.redroja.cjcrest.mapper;

import es.juventudcomunista.redroja.cjcrest.dto.HabilidadDTO;
import es.juventudcomunista.redroja.cjcrest.entity.Habilidad;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface HabilidadMapper {
    HabilidadMapper INSTANCE = Mappers.getMapper(HabilidadMapper.class);

    Habilidad toEntity(HabilidadDTO dto);

    HabilidadDTO toDTO(Habilidad entity);

    List<Habilidad> toEntities(List<HabilidadDTO> dtos);

    List<HabilidadDTO> toDTOs(List<Habilidad> entities);
}
