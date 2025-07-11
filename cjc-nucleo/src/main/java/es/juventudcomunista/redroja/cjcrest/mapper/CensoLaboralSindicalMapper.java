package es.juventudcomunista.redroja.cjcrest.mapper;

import es.juventudcomunista.redroja.cjcrest.dto.CensoLaboralSindicalDTO;
import es.juventudcomunista.redroja.cjcrest.entity.vistas.CensoLaboralSindical;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CensoLaboralSindicalMapper {
    CensoLaboralSindicalMapper INSTANCE = Mappers.getMapper(CensoLaboralSindicalMapper.class);

    CensoLaboralSindical toEntity(CensoLaboralSindicalDTO dto);

    @Mapping(source = "militanteId", target = "id")
    CensoLaboralSindicalDTO toDTO(CensoLaboralSindical entity);

    List<CensoLaboralSindical> toEntities(List<CensoLaboralSindicalDTO> dtos);

    @Mapping(source = "militanteId", target = "id")
    List<CensoLaboralSindicalDTO> toDTOs(List<CensoLaboralSindical> entities);
}
