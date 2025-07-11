package es.juventudcomunista.redroja.cjcrest.mapper;


import es.juventudcomunista.redroja.cjcrest.entity.SubsubdivisionNivelEducativo;
import es.juventudcomunista.redroja.cjcrest.web.dto.SubsubdivisionNivelEducativoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper
public interface SubsubdivisionNivelEducativoMapper {
    SubsubdivisionNivelEducativoMapper INSTANCE = Mappers.getMapper(SubsubdivisionNivelEducativoMapper.class);

    @Mapping(source = "padreId", target = "subdivisionNivelEducativo.id")
    SubsubdivisionNivelEducativo toEntity(SubsubdivisionNivelEducativoDTO dto);

    @Mapping(source = "subdivisionNivelEducativo.id", target = "padreId")
    SubsubdivisionNivelEducativoDTO toDTO(SubsubdivisionNivelEducativo entity);

    List<SubsubdivisionNivelEducativo> toEntities(List<SubsubdivisionNivelEducativoDTO> dtos);

    List<SubsubdivisionNivelEducativoDTO> toDTOs(List<SubsubdivisionNivelEducativo> entities);
}
