package es.juventudcomunista.redroja.cjcrest.mapper;


import es.juventudcomunista.redroja.cjcrest.entity.SubdivisionNivelEducativo;
import es.juventudcomunista.redroja.cjcrest.web.dto.SubdivisionNivelEducativoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper
public interface SubdivisionNivelEducativoMapper {
    SubdivisionNivelEducativoMapper INSTANCE = Mappers.getMapper(SubdivisionNivelEducativoMapper.class);

    @Mapping(source = "padreId", target = "nivelEducativo.id")
    SubdivisionNivelEducativo toEntity(SubdivisionNivelEducativoDTO dto);

    @Mapping(source = "nivelEducativo.id", target = "padreId")
    SubdivisionNivelEducativoDTO toDTO(SubdivisionNivelEducativo entity);


    List<SubdivisionNivelEducativo> toEntities(List<SubdivisionNivelEducativoDTO> dtos);

    List<SubdivisionNivelEducativoDTO> toDTOs(List<SubdivisionNivelEducativo> entities);
}
