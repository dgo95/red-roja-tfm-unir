package es.juventudcomunista.redroja.cjcrest.repository;

import es.juventudcomunista.redroja.cjcrest.entity.Municipio;
import es.juventudcomunista.redroja.cjcrest.entity.Provincia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MunicipioRepository extends JpaRepository<Municipio, Integer> {

	List<Municipio> findByProvincia(Provincia p);
	Optional<Municipio> findByNombre(String nombre);
}
