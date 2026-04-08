package com.tuapp.domain.repository;

import com.tuapp.infrastructure.persistence.entity.EnfermedadBaseEntity;

import java.util.List;

public interface EnfermedadBaseRepository {
    List<EnfermedadBaseEntity> findAllById(Iterable<Long> ids);
}
