package com.tuapp.infrastructure.persistence.repository;

import com.tuapp.domain.repository.CategoriaAnomaliaRepository;
import com.tuapp.infrastructure.persistence.entity.CategoriaAnomaliaEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CategoriaAnomaliaRepositoryImpl implements CategoriaAnomaliaRepository {

    private final CategoriaAnomaliaJpaRepository categoriaAnomaliaJpaRepository;

    public CategoriaAnomaliaRepositoryImpl(CategoriaAnomaliaJpaRepository categoriaAnomaliaJpaRepository) {
        this.categoriaAnomaliaJpaRepository = categoriaAnomaliaJpaRepository;
    }

    @Override
    public Optional<CategoriaAnomaliaEntity> findById(Long id) {
        return categoriaAnomaliaJpaRepository.findById(id);
    }
}
