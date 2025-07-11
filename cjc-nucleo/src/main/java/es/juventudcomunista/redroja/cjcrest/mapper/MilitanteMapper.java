package es.juventudcomunista.redroja.cjcrest.mapper;


import es.juventudcomunista.redroja.cjcrest.entity.Militante;
import es.juventudcomunista.redroja.cjcrest.web.dto.MilitanteDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface MilitanteMapper {
    MilitanteMapper INSTANCE = Mappers.getMapper(MilitanteMapper.class);

    Militante toEntity(MilitanteDTO dto);

    MilitanteDTO toDTO( Militante entity);

    List< Militante> toEntities(List<MilitanteDTO> dtos);

    List<MilitanteDTO> toDTOs(List< Militante> entities);
}
