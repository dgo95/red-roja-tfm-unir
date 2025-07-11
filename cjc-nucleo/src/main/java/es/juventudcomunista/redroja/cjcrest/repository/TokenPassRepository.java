package es.juventudcomunista.redroja.cjcrest.repository;

import es.juventudcomunista.redroja.cjcrest.entity.Militante;
import es.juventudcomunista.redroja.cjcrest.entity.TokenPass;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
public interface TokenPassRepository extends CrudRepository<TokenPass, Long> {

    boolean existsByToken(String token);

    void deleteByToken(String token);

    Optional<TokenPass> findByToken(String token);

    Optional<TokenPass> findByMilitante(Militante m);
}
