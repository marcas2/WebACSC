package com.tuapp.infrastructure.persistence.repository;

import com.tuapp.infrastructure.persistence.entity.FocoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FocoJpaRepository extends JpaRepository<FocoEntity, Long> {
	List<FocoEntity> findAllByOrderByNombreAsc();
	boolean existsByNombreIgnoreCase(String nombre);
	boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
	boolean existsByCodigoIgnoreCase(String codigo);
	boolean existsByCodigoIgnoreCaseAndIdNot(String codigo, Long id);
}
