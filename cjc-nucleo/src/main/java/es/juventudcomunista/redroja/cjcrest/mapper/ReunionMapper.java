
package es.juventudcomunista.redroja.cjcrest.mapper;

import es.juventudcomunista.redroja.cjcrest.dto.ReunionDTO;
import es.juventudcomunista.redroja.cjcrest.entity.Reunion;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.time.Duration;
import java.util.List;

import org.mapstruct.Mapping;

@Mapper
public interface ReunionMapper {
    ReunionMapper INSTANCE = Mappers.getMapper(ReunionMapper.class);

    @Mapping(target = "acta", ignore = true)
    @Mapping(target = "comiteBase", ignore = true)
    Reunion toEntity(ReunionDTO dto);


    ReunionDTO toDTO(Reunion entity);

    List<Reunion> toEntities(List<ReunionDTO> dtos);

    List<ReunionDTO> toDTOs(List<Reunion> entities);

    default Integer map(Duration duration) {
        return duration != null ? (int) duration.toHours() : null;
    }

    default Duration map(Integer hours) {
        return hours != null ? Duration.ofHours(hours) : null;
    }
}
