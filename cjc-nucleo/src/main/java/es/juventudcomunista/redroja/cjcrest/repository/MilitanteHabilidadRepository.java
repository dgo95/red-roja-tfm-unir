package es.juventudcomunista.redroja.cjcrest.repository;


import es.juventudcomunista.redroja.cjcrest.entity.Habilidad;
import es.juventudcomunista.redroja.cjcrest.entity.Militante;
import es.juventudcomunista.redroja.cjcrest.entity.MilitanteHabilidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MilitanteHabilidadRepository extends JpaRepository<MilitanteHabilidad, Long> {
    Optional<MilitanteHabilidad> findByMilitanteAndHabilidad(Militante m, Habilidad habilidad);

    List<MilitanteHabilidad> findByMilitante(Militante m);
}
