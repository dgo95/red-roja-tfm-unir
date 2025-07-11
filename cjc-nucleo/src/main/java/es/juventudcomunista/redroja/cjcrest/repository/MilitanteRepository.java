package es.juventudcomunista.redroja.cjcrest.repository;

import es.juventudcomunista.redroja.cjcrest.entity.ComiteBase;
import es.juventudcomunista.redroja.cjcrest.entity.Militante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MilitanteRepository extends JpaRepository<Militante, Integer> {

    Optional<Militante>  findByNumeroCarnet(String carnet);

    List<Militante> findByComitesBase(ComiteBase c);

    List<Militante> findByComitesBaseAndPremilitanteFalse(ComiteBase c);

    List<Militante> findByComitesBaseAndPremilitanteTrue(ComiteBase c);
    
    @Query("SELECT m.id FROM Militante m WHERE :comiteBase MEMBER OF m.comitesBase")
    List<Integer> findIdsByComitesBase(@Param("comiteBase") ComiteBase comiteBase);

    Optional<Militante> findByMilitanteId(String id);

}
