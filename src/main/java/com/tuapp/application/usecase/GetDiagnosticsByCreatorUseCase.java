package com.tuapp.application.usecase;

import com.tuapp.domain.repository.DiagnosticRepository;
import com.tuapp.infrastructure.persistence.entity.DiagnosticEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetDiagnosticsByCreatorUseCase {

    private final DiagnosticRepository diagnosticRepository;

    public GetDiagnosticsByCreatorUseCase(DiagnosticRepository diagnosticRepository) {
        this.diagnosticRepository = diagnosticRepository;
    }

    public List<DiagnosticEntity> execute(Long creatorId) {
        if (creatorId == null) {
            return diagnosticRepository.findAllWithCreatorOrderByCreatedAtDesc();
        }
        return diagnosticRepository.findAllByCreatorIdWithCreatorOrderByCreatedAtDesc(creatorId);
    }
}