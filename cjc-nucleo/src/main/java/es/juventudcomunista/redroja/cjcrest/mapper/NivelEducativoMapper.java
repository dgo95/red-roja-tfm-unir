package es.juventudcomunista.redroja.cjcrest.mapper;


import es.juventudcomunista.redroja.cjcrest.entity.NivelEducativo;
import es.juventudcomunista.redroja.cjcrest.web.dto.NivelEducativoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface NivelEducativoMapper {
    NivelEducativoMapper INSTANCE = Mappers.getMapper(NivelEducativoMapper.class);

    NivelEducativo toEntity(NivelEducativoDTO dto);

    NivelEducativoDTO toDTO(NivelEducativo entity);

    List<NivelEducativo> toEntities(List<NivelEducativoDTO> dtos);

    List<NivelEducativoDTO> toDTOs(List<NivelEducativo> entities);
}



