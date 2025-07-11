package es.juventudcomunista.redroja.cjcrest.repository;


import es.juventudcomunista.redroja.cjcrest.entity.IdiomaConocido;
import es.juventudcomunista.redroja.cjcrest.entity.Militante;
import es.juventudcomunista.redroja.cjcrest.entity.MilitanteIdioma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MilitanteIdiomaRepository extends JpaRepository<MilitanteIdioma, Long> {
    Optional<MilitanteIdioma> findByMilitanteAndIdiomaConocido(Militante m, IdiomaConocido idioma);
}
