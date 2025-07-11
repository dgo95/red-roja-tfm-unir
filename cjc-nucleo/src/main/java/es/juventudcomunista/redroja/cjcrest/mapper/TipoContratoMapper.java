
package es.juventudcomunista.redroja.cjcrest.mapper;

import es.juventudcomunista.redroja.cjcrest.entity.TipoContrato;
import es.juventudcomunista.redroja.cjcrest.web.dto.TipoContratoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
@Mapper
public interface TipoContratoMapper {
    TipoContratoMapper INSTANCE = Mappers.getMapper(TipoContratoMapper.class);

    TipoContrato toEntity(TipoContratoDTO dto);

    TipoContratoDTO toDTO(TipoContrato entity);

    List<TipoContrato> toEntities(List<TipoContratoDTO> dtos);

    List<TipoContratoDTO> toDTOs(List<TipoContrato> entities);
}
