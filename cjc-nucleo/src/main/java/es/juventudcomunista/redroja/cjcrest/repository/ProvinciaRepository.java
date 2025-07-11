package es.juventudcomunista.redroja.cjcrest.repository;

import es.juventudcomunista.redroja.cjcrest.entity.ComunidadAutonoma;
import es.juventudcomunista.redroja.cjcrest.entity.Provincia;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProvinciaRepository extends CrudRepository<Provincia, Integer> {

	List<Provincia> findByComunidadAutonoma(ComunidadAutonoma id);
}