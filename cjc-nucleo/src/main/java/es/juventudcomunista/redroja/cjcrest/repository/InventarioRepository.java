package es.juventudcomunista.redroja.cjcrest.repository;

import es.juventudcomunista.redroja.cjcrest.entity.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventarioRepository extends JpaRepository<Inventario, Integer> {
}
