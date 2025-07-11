package es.juventudcomunista.redroja.cjcrest.repository;

import es.juventudcomunista.redroja.cjcrest.entity.ComiteBase;
import es.juventudcomunista.redroja.cjcrest.entity.Reunion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReunionRepository extends JpaRepository<Reunion, Integer> {

    @Query("SELECT r FROM Reunion r WHERE r.id IN (SELECT ru.id FROM ComiteBase c JOIN c.reuniones ru WHERE c = :comite)")
    List<Reunion> findByComiteBaseOrderByFechaInicioDesc(@Param("comite")ComiteBase c, Pageable topFive);

    List<Reunion> findByTerminadaFalseAndFechaInicioBefore(LocalDateTime fecha);
 }
