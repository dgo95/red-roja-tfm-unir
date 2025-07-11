package es.juventudcomunista.redroja.cjcrest.repository;

import es.juventudcomunista.redroja.cjcrest.entity.Militante;
import es.juventudcomunista.redroja.cjcrest.entity.Sindicacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SindicacionRepository extends JpaRepository<Sindicacion, Integer> {

    Optional<Sindicacion> findByMilitante(Militante militante);

    void deleteByMilitante(Militante m);

}
