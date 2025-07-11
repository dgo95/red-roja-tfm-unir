package es.juventudcomunista.redroja.cjcdocumentos.repository;

import es.juventudcomunista.redroja.cjcdocumentos.entity.Documento;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long>, JpaSpecificationExecutor<Documento> {
    Optional<Documento> findByUuid(String uuid);
    List<Documento> findByPropietario(String propietario);
}