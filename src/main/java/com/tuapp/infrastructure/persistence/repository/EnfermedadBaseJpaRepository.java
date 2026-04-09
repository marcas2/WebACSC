package com.tuapp.infrastructure.persistence.repository;

import com.tuapp.infrastructure.persistence.entity.EnfermedadBaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnfermedadBaseJpaRepository extends JpaRepository<EnfermedadBaseEntity, Long> {
	List<EnfermedadBaseEntity> findAllByOrderByNombreAsc();
	boolean existsByNombreIgnoreCase(String nombre);
	boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}
