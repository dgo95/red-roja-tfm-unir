package es.juventudcomunista.redroja.cjcrest.repository;

import es.juventudcomunista.redroja.cjcrest.entity.vistas.CensoEstudiantil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CensoEstudiantilRepository extends JpaRepository<CensoEstudiantil, Integer> {

    @Query("SELECT c FROM CensoEstudiantil c WHERE c.id IN :ids")
    Page<CensoEstudiantil> findByIdIn(List<Integer> ids, Pageable pageable);
}
