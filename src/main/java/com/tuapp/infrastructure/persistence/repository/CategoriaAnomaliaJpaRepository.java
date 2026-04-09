package com.tuapp.infrastructure.persistence.repository;

import com.tuapp.infrastructure.persistence.entity.CategoriaAnomaliaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaAnomaliaJpaRepository extends JpaRepository<CategoriaAnomaliaEntity, Long> {
	List<CategoriaAnomaliaEntity> findAllByOrderByNombreAsc();
	boolean existsByNombreIgnoreCase(String nombre);
	boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
	boolean existsByCodigoIgnoreCase(String codigo);
	boolean existsByCodigoIgnoreCaseAndIdNot(String codigo, Long id);
}
