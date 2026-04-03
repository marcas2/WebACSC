package com.tuapp.application.usecase;

import com.tuapp.domain.repository.DiagnosticRepository;
import com.tuapp.infrastructure.persistence.entity.DiagnosticEntity;
import com.tuapp.presentation.dto.CreateDiagnosticRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateDiagnosticUseCase {

    private final DiagnosticRepository diagnosticRepository;

    public CreateDiagnosticUseCase(DiagnosticRepository diagnosticRepository) {
        this.diagnosticRepository = diagnosticRepository;
    }

    @Transactional
    public DiagnosticEntity execute(CreateDiagnosticRequest request) {
        DiagnosticEntity entity = new DiagnosticEntity();
        entity.setIsNormal(request.getIsNormal());
        entity.setAge(request.getAge());
        entity.setGender(normalizeGender(request.getGender()));
        entity.setUnderlyingDiseases(normalizeUnderlyingDiseases(request.getUnderlyingDiseases()));

        return diagnosticRepository.save(entity);
    }

    private String normalizeGender(String gender) {
        return gender == null ? null : gender.trim().toUpperCase();
    }

    private String normalizeUnderlyingDiseases(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}