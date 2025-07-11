package es.juventudcomunista.redroja.cjcrest.repository;

import es.juventudcomunista.redroja.cjcrest.entity.vistas.CensoGeneral;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CensoGeneralRepository extends JpaRepository<CensoGeneral, Integer> {
    @Query("SELECT c FROM CensoGeneral c WHERE c.id IN :ids")
    Page<CensoGeneral> findByIdIn(@Param("ids") List<Integer> ids, Pageable pageable);
}

