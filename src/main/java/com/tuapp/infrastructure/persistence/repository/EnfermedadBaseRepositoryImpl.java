package com.tuapp.infrastructure.persistence.repository;

import com.tuapp.domain.repository.EnfermedadBaseRepository;
import com.tuapp.infrastructure.persistence.entity.EnfermedadBaseEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EnfermedadBaseRepositoryImpl implements EnfermedadBaseRepository {

    private final EnfermedadBaseJpaRepository enfermedadBaseJpaRepository;

    public EnfermedadBaseRepositoryImpl(EnfermedadBaseJpaRepository enfermedadBaseJpaRepository) {
        this.enfermedadBaseJpaRepository = enfermedadBaseJpaRepository;
    }

    @Override
    public List<EnfermedadBaseEntity> findAllById(Iterable<Long> ids) {
        return enfermedadBaseJpaRepository.findAllById(ids);
    }
}
