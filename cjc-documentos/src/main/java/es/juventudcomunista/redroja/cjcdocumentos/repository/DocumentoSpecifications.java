package es.juventudcomunista.redroja.cjcdocumentos.repository;

import es.juventudcomunista.redroja.cjcdocumentos.entity.Documento;
import es.juventudcomunista.redroja.cjccommonutils.enums.*;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Set;

public final class DocumentoSpecifications {

    private DocumentoSpecifications() {} // util

    public static Specification<Documento> propietarioIn(List<String> propietarios) {
        return (propietarios == null || propietarios.isEmpty())
                ? null
                : (root, query, cb) -> root.get("propietario").in(propietarios);
    }

    public static Specification<Documento> nivelEq(NivelDocumento nivel) {
        return (nivel == null)
                ? null
                : (root, query, cb) -> cb.equal(root.get("nivel"), nivel);
    }

    public static Specification<Documento> confidencialidadEq(Confidencialidad c) {
        return (c == null)
                ? null
                : (root, query, cb) -> cb.equal(root.get("confidencialidad"), c);
    }

    public static Specification<Documento> tipoIn(List<TipoDocumento> tipos) {
        return (tipos == null || tipos.isEmpty())
                ? null
                : (root, query, cb) -> root.get("tipo").in(tipos);
    }

    public static Specification<Documento> categoriaIn(List<Categoria> categorias) {
        return (categorias == null || categorias.isEmpty())
                ? null
                : (root, query, cb) -> {
                    // join a la colección de categorías
                    var join = root.joinSet("categorias", JoinType.INNER);
                    return join.in(Set.copyOf(categorias));
                };
    }

    public static Specification<Documento> textoLike(String texto) {
        return (texto == null || texto.isBlank())
                ? null
                : (root, query, cb) -> {
                    String patron = "%" + texto.toLowerCase() + "%";
                    return cb.or(
                            cb.like(cb.lower(root.get("nombreOriginal")), patron),
                            cb.like(cb.lower(root.get("uuid")), patron)
                    );
                };
    }
}