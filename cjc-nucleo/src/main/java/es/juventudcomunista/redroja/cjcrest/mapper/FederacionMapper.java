
package es.juventudcomunista.redroja.cjcrest.mapper;

import es.juventudcomunista.redroja.cjcrest.entity.Federacion;
import es.juventudcomunista.redroja.cjcrest.web.dto.FederacionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
@Mapper
public interface FederacionMapper {
    FederacionMapper INSTANCE = Mappers.getMapper(FederacionMapper.class);

    Federacion toEntity(FederacionDTO dto);

    FederacionDTO toDTO(Federacion entity);

    List<Federacion> toEntities(List<FederacionDTO> dtos);

    List<FederacionDTO> toDTOs(List<Federacion> entities);
}
