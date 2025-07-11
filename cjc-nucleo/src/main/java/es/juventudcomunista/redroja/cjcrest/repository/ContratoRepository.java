package es.juventudcomunista.redroja.cjcrest.repository;

import es.juventudcomunista.redroja.cjcrest.entity.Contrato;
import es.juventudcomunista.redroja.cjcrest.entity.Militante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContratoRepository extends JpaRepository<Contrato, Integer> {

    Optional<Contrato> findByMilitante(Militante militante);

    void deleteByMilitante(Militante m);

}
