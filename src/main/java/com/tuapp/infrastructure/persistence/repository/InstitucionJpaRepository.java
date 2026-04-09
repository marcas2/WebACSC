package com.tuapp.infrastructure.persistence.repository;

import com.tuapp.infrastructure.persistence.entity.InstitucionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InstitucionJpaRepository extends JpaRepository<InstitucionEntity, Long> {
    List<InstitucionEntity> findAllByOrderByNombreAsc();
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}
