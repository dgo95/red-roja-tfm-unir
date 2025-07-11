package es.juventudcomunista.redroja.cjcrest.repository;

import es.juventudcomunista.redroja.cjcrest.entity.Sindicato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SindicatoRepository extends JpaRepository<Sindicato, Integer> {

	boolean existsByNombre(String nombre);

	Optional<Sindicato> findByNombre(String nombre);

}
