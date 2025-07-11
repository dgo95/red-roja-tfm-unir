package es.juventudcomunista.redroja.cjcrest.repository;

import es.juventudcomunista.redroja.cjcrest.entity.IdiomaConocido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdiomaRepository extends JpaRepository<IdiomaConocido, Integer> {
}
