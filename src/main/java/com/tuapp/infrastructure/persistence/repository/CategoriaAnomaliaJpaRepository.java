package com.tuapp.infrastructure.persistence.repository;

import com.tuapp.infrastructure.persistence.entity.CategoriaAnomaliaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaAnomaliaJpaRepository extends JpaRepository<CategoriaAnomaliaEntity, Long> {
}
