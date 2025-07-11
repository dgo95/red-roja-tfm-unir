package es.juventudcomunista.redroja.cjcrest.mapper;

import es.juventudcomunista.redroja.cjcrest.dto.FichaMovilidadInputDTO;
import es.juventudcomunista.redroja.cjcrest.entity.FichaMovilidad;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FichaMovilidadMapper {
    FichaMovilidadMapper INSTANCE = Mappers.getMapper(FichaMovilidadMapper.class);

    @Mappings({
            @Mapping(source = "municipio", target = "municipio.id")
    })
    FichaMovilidad toEntity(FichaMovilidadInputDTO dto);

    @Mappings({
            @Mapping(source = "municipio.id", target = "municipio"),
            @Mapping(target = "provincia", ignore = true),
            @Mapping(target = "comunidadAutonoma", ignore = true),
            @Mapping(target = "municipios", ignore = true),
            @Mapping(target = "provincias", ignore = true),
            @Mapping(target = "comunidades", ignore = true)
    })
    FichaMovilidadInputDTO toDTO(FichaMovilidad entity);
}