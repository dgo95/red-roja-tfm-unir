package es.juventudcomunista.redroja.cjcrest.repository;

import es.juventudcomunista.redroja.cjcrest.entity.vistas.CensoLaboralSindical;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CensoLaboralSindicalRepository extends JpaRepository<CensoLaboralSindical, Integer> {

    @Query("SELECT c FROM CensoLaboralSindical c WHERE c.id IN :ids")
    Page<CensoLaboralSindical> findByIdIn(List<Integer> ids, Pageable pageable);

}
