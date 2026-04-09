package com.tuapp.infrastructure.persistence.repository;

import com.tuapp.infrastructure.persistence.entity.ConsultorioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsultorioJpaRepository extends JpaRepository<ConsultorioEntity, Long> {
    List<ConsultorioEntity> findByInstitucionIdOrderByNombreAsc(Long institucionId);
    boolean existsByCodigoIgnoreCase(String codigo);
    boolean existsByCodigoIgnoreCaseAndIdNot(String codigo, Long id);
    boolean existsByNombreIgnoreCaseAndInstitucionId(String nombre, Long institucionId);
    boolean existsByNombreIgnoreCaseAndInstitucionIdAndIdNot(String nombre, Long institucionId, Long id);
}
