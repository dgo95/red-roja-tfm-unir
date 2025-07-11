package es.juventudcomunista.redroja.cjcrest.repository;

import es.juventudcomunista.redroja.cjcrest.entity.Federacion;
import es.juventudcomunista.redroja.cjcrest.entity.Sindicato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FederacionRepository extends JpaRepository<Federacion, Integer> {

    List<Federacion> findBySindicato(Sindicato sindicato);

}
