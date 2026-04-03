package com.tuapp.application.service;

import com.tuapp.infrastructure.persistence.repository.DiagnosticJpaRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardDiagnosticService {

    private final DiagnosticJpaRepository diagnosticRepository;

    public DashboardDiagnosticService(DiagnosticJpaRepository diagnosticRepository) {
        this.diagnosticRepository = diagnosticRepository;
    }

    public Map<String, Long> getByAgeRange() {
        return toMap(diagnosticRepository.countValvulopathiesByAgeRange());
    }

    public Map<String, Long> getByGender() {
        return toMap(diagnosticRepository.countValvulopathiesByGender());
    }

    public Map<String, Long> getByUnderlyingDiseases() {
        return toMap(diagnosticRepository.countValvulopathiesByUnderlyingDiseases());
    }

    /**
     * Verifica si existen datos de valvulopatías en la base de datos.
     * Cubre el paso "Validar existencia de datos" del proceso.
     */
    public boolean hasData() {
        return diagnosticRepository.countByIsNormalFalse() > 0;
    }

    /**
     * Verifica completitud: todos los rangos deben tener al menos un registro.
     * Cubre el paso "Verificar completitud de los datos" del proceso.
     */
    public boolean isDataComplete(Map<String, Long> ageMap,
                                   Map<String, Long> genderMap,
                                   Map<String, Long> diseaseMap) {
        return !ageMap.isEmpty() && !genderMap.isEmpty() && !diseaseMap.isEmpty();
    }

    private Map<String, Long> toMap(List<Object[]> rows) {
        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : rows) {
            result.put(String.valueOf(row[0]), ((Number) row[1]).longValue());
        }
        return result;
    }
}