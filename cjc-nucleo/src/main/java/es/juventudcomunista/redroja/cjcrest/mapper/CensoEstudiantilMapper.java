package es.juventudcomunista.redroja.cjcrest.mapper;

import es.juventudcomunista.redroja.cjcrest.dto.CensoEstudiantilDTO;
import es.juventudcomunista.redroja.cjcrest.entity.vistas.CensoEstudiantil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CensoEstudiantilMapper {
    CensoEstudiantilMapper INSTANCE = Mappers.getMapper(CensoEstudiantilMapper.class);

    CensoEstudiantil toEntity(CensoEstudiantilDTO dto);

    @Mapping(source = "militanteId", target = "id")
    CensoEstudiantilDTO toDTO(CensoEstudiantil entity);

    List<CensoEstudiantil> toEntities(List<CensoEstudiantilDTO> dtos);

    @Mapping(source = "militanteId", target = "id")
    List<CensoEstudiantilDTO> toDTOs(List<CensoEstudiantil> entities);
}
