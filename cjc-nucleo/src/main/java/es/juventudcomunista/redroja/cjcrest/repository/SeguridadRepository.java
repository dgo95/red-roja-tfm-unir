package es.juventudcomunista.redroja.cjcrest.repository;


import es.juventudcomunista.redroja.cjcrest.entity.Militante;
import es.juventudcomunista.redroja.cjcrest.entity.Seguridad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeguridadRepository extends JpaRepository<Seguridad, Integer> {

	
	Seguridad getByMilitante(Militante m);
}
