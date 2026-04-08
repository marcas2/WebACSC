package com.tuapp.infrastructure.persistence.repository;

import com.tuapp.domain.repository.FocoRepository;
import com.tuapp.infrastructure.persistence.entity.FocoEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class FocoRepositoryImpl implements FocoRepository {

    private final FocoJpaRepository focoJpaRepository;

    public FocoRepositoryImpl(FocoJpaRepository focoJpaRepository) {
        this.focoJpaRepository = focoJpaRepository;
    }

    @Override
    public Optional<FocoEntity> findById(Long id) {
        return focoJpaRepository.findById(id);
    }
}
