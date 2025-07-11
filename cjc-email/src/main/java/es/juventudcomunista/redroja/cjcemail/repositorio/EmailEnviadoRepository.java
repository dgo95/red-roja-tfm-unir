package es.juventudcomunista.redroja.cjcemail.repositorio;

import es.juventudcomunista.redroja.cjcemail.entity.email.EmailEnviado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailEnviadoRepository extends JpaRepository<EmailEnviado, Long> {

}

