package com.tuapp.domain.repository;

import com.tuapp.infrastructure.persistence.entity.CategoriaAnomaliaEntity;

import java.util.Optional;

public interface CategoriaAnomaliaRepository {
    Optional<CategoriaAnomaliaEntity> findById(Long id);
}
