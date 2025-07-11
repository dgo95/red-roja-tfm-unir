
package es.juventudcomunista.redroja.cjcrest.mapper;

import es.juventudcomunista.redroja.cjcrest.entity.Municipio;
import es.juventudcomunista.redroja.cjcrest.web.dto.MunicipioDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
@Mapper
public interface MunicipioMapper {
    MunicipioMapper INSTANCE = Mappers.getMapper(MunicipioMapper.class);

    Municipio toEntity(MunicipioDTO dto);

    MunicipioDTO toDTO(Municipio entity);

    List<Municipio> toEntities(List<MunicipioDTO> dtos);

    List<MunicipioDTO> toDTOs(List<Municipio> entities);
}
