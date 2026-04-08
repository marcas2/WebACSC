package com.tuapp.domain.repository;

import com.tuapp.infrastructure.persistence.entity.FocoEntity;

import java.util.Optional;

public interface FocoRepository {
    Optional<FocoEntity> findById(Long id);
}
