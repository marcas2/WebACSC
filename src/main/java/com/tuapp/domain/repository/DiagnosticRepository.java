package com.tuapp.domain.repository;

import com.tuapp.infrastructure.persistence.entity.DiagnosticEntity;

import java.util.List;
import java.util.Optional;

public interface DiagnosticRepository {
    DiagnosticEntity save(DiagnosticEntity diagnostic);
    Optional<DiagnosticEntity> findById(Long diagnosticId);
    List<DiagnosticEntity> findAllWithCreatorOrderByCreatedAtDesc();
    List<DiagnosticEntity> findAllByCreatorIdWithCreatorOrderByCreatedAtDesc(Long creatorId);
}