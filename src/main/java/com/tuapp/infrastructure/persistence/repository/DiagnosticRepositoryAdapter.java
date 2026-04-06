package com.tuapp.infrastructure.persistence.repository;

import com.tuapp.domain.repository.DiagnosticRepository;
import com.tuapp.infrastructure.persistence.entity.DiagnosticEntity;
import org.springframework.stereotype.Repository;

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
}