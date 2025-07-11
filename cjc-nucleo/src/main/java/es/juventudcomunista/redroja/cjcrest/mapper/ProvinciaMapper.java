
package es.juventudcomunista.redroja.cjcrest.mapper;

import es.juventudcomunista.redroja.cjcrest.entity.Provincia;
import es.juventudcomunista.redroja.cjcrest.web.dto.ProvinciaDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ProvinciaMapper {
    ProvinciaMapper INSTANCE = Mappers.getMapper(ProvinciaMapper.class);

    Provincia toEntity(ProvinciaDTO dto);

    ProvinciaDTO toDTO(Provincia entity);

    List<Provincia> toEntities(List<ProvinciaDTO> dtos);

    List<ProvinciaDTO> toDTOs(List<Provincia> entities);
}
