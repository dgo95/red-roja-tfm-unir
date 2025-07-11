package es.juventudcomunista.redroja.cjcrest.mapper;

import es.juventudcomunista.redroja.cjcrest.entity.vistas.CensoGeneral;
import es.juventudcomunista.redroja.cjcrest.dto.CensoGeneralDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CensoGeneralMapper {
    CensoGeneralMapper INSTANCE = Mappers.getMapper(CensoGeneralMapper.class);

    CensoGeneral toEntity(CensoGeneralDTO dto);

    @Mapping(source = "militanteId", target = "id")
    @Mapping(target = "habilidades", ignore = true)
    @Mapping(target = "fichaMovilidad", ignore = true)
    CensoGeneralDTO toDTO(CensoGeneral entity);

    @Mapping(target = "habilidades", ignore = true)
    List<CensoGeneral> toEntities(List<CensoGeneralDTO> dtos);

    @Mapping(source = "militanteId", target = "id")
    @Mapping(target = "habilidades", ignore = true)
    List<CensoGeneralDTO> toDTOs(List<CensoGeneral> entities);
}
