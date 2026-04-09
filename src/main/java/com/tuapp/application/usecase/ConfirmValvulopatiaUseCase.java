package com.tuapp.application.usecase;

import com.tuapp.domain.repository.DiagnosticRepository;
import com.tuapp.infrastructure.persistence.entity.DiagnosticEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfirmValvulopatiaUseCase {

    private final DiagnosticRepository diagnosticRepository;

    public ConfirmValvulopatiaUseCase(DiagnosticRepository diagnosticRepository) {
        this.diagnosticRepository = diagnosticRepository;
    }

    @Transactional
    public DiagnosticEntity execute(Long diagnosticId, Boolean valvulopatia) {
        DiagnosticEntity diagnostic = diagnosticRepository.findById(diagnosticId)
                .orElseThrow(() -> new IllegalArgumentException("Diagnóstico no encontrado"));

        diagnostic.setVerificado(true);
        diagnostic.setValvulopatia(valvulopatia);

        return diagnosticRepository.save(diagnostic);
    }
}