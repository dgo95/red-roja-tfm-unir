
package es.juventudcomunista.redroja.cjcrest.mapper;

import es.juventudcomunista.redroja.cjcrest.entity.ModalidadTrabajo;
import es.juventudcomunista.redroja.cjcrest.web.dto.ModalidadTrabajoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
@Mapper
public interface ModalidadTrabajoMapper {
    ModalidadTrabajoMapper INSTANCE = Mappers.getMapper(ModalidadTrabajoMapper.class);

    ModalidadTrabajo toEntity(ModalidadTrabajoDTO dto);

    ModalidadTrabajoDTO toDTO(ModalidadTrabajo entity);

    List<ModalidadTrabajo> toEntities(List<ModalidadTrabajoDTO> dtos);

    List<ModalidadTrabajoDTO> toDTOs(List<ModalidadTrabajo> entities);
}
