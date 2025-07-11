package es.juventudcomunista.redroja.cjcrest.repository;

import es.juventudcomunista.redroja.cjcrest.entity.ComiteBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ColectivoRepository extends JpaRepository<ComiteBase, Integer> {
	Optional<ComiteBase> findByNombre(String string);

	/* existsByNombre:obtiene si un colectivo existe dado su nombre */
	boolean existsByNombre(String nombre);
	
	@Query("SELECT c FROM ComiteBase c JOIN c.reuniones r WHERE r.id = :reunionId")
    Optional<ComiteBase> findByReunionId(@Param("reunionId") Integer reunionId);

    Set<ComiteBase> findByIdIn(Set<Integer> comitesBaseIds);
}
