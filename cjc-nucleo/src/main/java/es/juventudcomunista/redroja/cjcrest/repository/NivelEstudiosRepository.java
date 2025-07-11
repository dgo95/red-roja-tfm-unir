package es.juventudcomunista.redroja.cjcrest.repository;

import es.juventudcomunista.redroja.cjcrest.entity.NivelEducativo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NivelEstudiosRepository extends JpaRepository<NivelEducativo, Integer> {

}
