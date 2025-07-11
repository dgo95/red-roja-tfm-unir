package es.juventudcomunista.redroja.cjcrest.repository;

import es.juventudcomunista.redroja.cjcrest.entity.ComiteBase;
import es.juventudcomunista.redroja.cjcrest.entity.Militante;
import es.juventudcomunista.redroja.cjcrest.entity.MilitanteRolComite;
import es.juventudcomunista.redroja.cjccommonutils.enums.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MilitanteRolComiteRepository extends JpaRepository<MilitanteRolComite, Integer> {
    Optional<MilitanteRolComite> findByComiteBaseAndRol(ComiteBase c, Rol rol);

    List<MilitanteRolComite> findByMilitante(Militante m);
}
