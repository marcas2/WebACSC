package com.tuapp.domain.repository;

import com.tuapp.infrastructure.persistence.entity.DiagnosticEntity;

public interface DiagnosticRepository {
    DiagnosticEntity save(DiagnosticEntity diagnostic);
}