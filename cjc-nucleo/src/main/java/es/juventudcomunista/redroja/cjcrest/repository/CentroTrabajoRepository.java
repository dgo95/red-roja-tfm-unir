package es.juventudcomunista.redroja.cjcrest.repository;

import es.juventudcomunista.redroja.cjcrest.entity.CentroTrabajo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface CentroTrabajoRepository  extends CrudRepository<CentroTrabajo, Long> {

}
