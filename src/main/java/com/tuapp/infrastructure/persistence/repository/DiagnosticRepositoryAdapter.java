package com.tuapp.infrastructure.persistence.repository;

import com.tuapp.domain.repository.DiagnosticRepository;
import com.tuapp.infrastructure.persistence.entity.DiagnosticEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DiagnosticRepositoryAdapter implements DiagnosticRepository {

    private final DiagnosticJpaRepository diagnosticJpaRepository;

    public DiagnosticRepositoryAdapter(DiagnosticJpaRepository diagnosticJpaRepository) {
        this.diagnosticJpaRepository = diagnosticJpaRepository;
    }

    @Override
    public DiagnosticEntity save(DiagnosticEntity diagnostic) {
        return diagnosticJpaRepository.save(diagnostic);

    }

    @Override
    public Optional<DiagnosticEntity> findById(Long diagnosticId) {
        return diagnosticJpaRepository.findById(diagnosticId);
    }

    @Override
    public List<DiagnosticEntity> findAllWithCreatorOrderByCreatedAtDesc() {
        return diagnosticJpaRepository.findAllWithUsuarioCrea();

    }

    @Override
    public List<DiagnosticEntity> findAllByCreatorIdWithCreatorOrderByCreatedAtDesc(Long creatorId) {
        return diagnosticJpaRepository.findAllByUsuarioCreaIdWithUsuarioCrea(creatorId);
    }
}