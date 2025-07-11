package es.juventudcomunista.redroja.cjcrest.mapper;

import es.juventudcomunista.redroja.cjcrest.dto.ContactoDTO;
import es.juventudcomunista.redroja.cjcrest.entity.Contacto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ContactoMapper {

    ContactoMapper INSTANCE = Mappers.getMapper(ContactoMapper.class);

    @Mapping(source = "municipio.id", target = "municipio")
    @Mapping(source = "encargadoSeguimiento.militanteId", target = "militanteId")
    ContactoDTO toDTO(Contacto contacto);
}

