package es.juventudcomunista.redroja.cjcrest.mapper;

import es.juventudcomunista.redroja.cjcrest.dto.ComiteBaseDTO;
import es.juventudcomunista.redroja.cjcrest.entity.ComiteBase;
import es.juventudcomunista.redroja.cjcrest.entity.MilitanteRolComite;
import es.juventudcomunista.redroja.cjcrest.entity.Reunion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public interface ComiteBaseMapper {

    ComiteBaseMapper INSTANCE = Mappers.getMapper(ComiteBaseMapper.class);

    @Mapping(source = "comiteDependiente.id", target = "comiteDependienteId")
    @Mapping(source = "responsabilidades", target = "responsabilidadIds", qualifiedByName = "mapResponsabilidadesToIds")
    ComiteBaseDTO toDto(ComiteBase comiteBase);

    List<ComiteBaseDTO> toDtoList(List<ComiteBase> comiteBaseList);

    List<ComiteBase> toEntityList(List<ComiteBaseDTO> comiteBaseDTOList);

    @Named("mapResponsabilidadesToIds")
    default Set<String> mapResponsabilidadesToIds(Set<MilitanteRolComite> responsabilidades) {
        return responsabilidades != null ? responsabilidades.stream().map(rol -> rol.getMilitante().getMilitanteId()).collect(Collectors.toSet()) : null;
    }

    @Named("mapIdsToReuniones")
    default Set<Reunion> mapIdsToReuniones(Set<Integer> ids) {
        return ids != null ? ids.stream().map(id -> {
            Reunion reunion = new Reunion();
            reunion.setId(id);
            return reunion;
        }).collect(Collectors.toSet()) : null;
    }

    @Named("mapIdsToResponsabilidades")
    default Set<MilitanteRolComite> mapIdsToResponsabilidades(Set<Integer> ids) {
        return ids != null ? ids.stream().map(id -> {
            MilitanteRolComite responsabilidad = new MilitanteRolComite();
            responsabilidad.setId(id);
            return responsabilidad;
        }).collect(Collectors.toSet()) : null;
    }
}

