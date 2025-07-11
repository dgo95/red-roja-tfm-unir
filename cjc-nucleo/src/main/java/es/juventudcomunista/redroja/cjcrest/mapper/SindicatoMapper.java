
package es.juventudcomunista.redroja.cjcrest.mapper;

import es.juventudcomunista.redroja.cjcrest.entity.Sindicato;
import es.juventudcomunista.redroja.cjcrest.web.dto.SindicatoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
@Mapper
public interface SindicatoMapper {
    SindicatoMapper INSTANCE = Mappers.getMapper(SindicatoMapper.class);

    Sindicato toEntity(SindicatoDTO dto);

    SindicatoDTO toDTO(Sindicato entity);

    List<Sindicato> toEntities(List<SindicatoDTO> dtos);

    List<SindicatoDTO> toDTOs(List<Sindicato> entities);
}
