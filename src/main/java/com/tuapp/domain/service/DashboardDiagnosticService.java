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

    public long getTotalRegistros() {
        return diagnosticRepository.count();
    }

    public Map<String, Long> getByNormalStatus() {
        return toMap(diagnosticRepository.countByNormalStatus());
    }

    public Map<String, Long> getValvulopathiesByAgeRange() {
        return toMap(diagnosticRepository.countValvulopathiesByAgeRange());
    }

    public Map<String, Long> getValvulopathiesByGender() {
        return toMap(diagnosticRepository.countValvulopathiesByGender());
    }

    public Map<String, Long> getValvulopathiesByUnderlyingDiseases() {
        return toMap(diagnosticRepository.countValvulopathiesByUnderlyingDiseases());
    }

    public Map<String, Long> getByCategoriaAnomalia() {
        return toMap(diagnosticRepository.countByCategoriaAnomalia());
    }

    public Map<String, Long> getByFoco() {
        return toMap(diagnosticRepository.countByFoco());
    }

    public Map<String, Long> getByInstitucion() {
        return toMap(diagnosticRepository.countByInstitucion());
    }

    public Map<String, Long> getByMonth() {
        return toMap(diagnosticRepository.countByMonth());
    }

    /**
     * Verifica si existen registros en la base de datos.
     * Cubre el paso "Validar existencia de datos" del proceso.
     */
    public boolean hasData() {
        return diagnosticRepository.count() > 0;
    }

    /**
     * Verifica completitud: todos los rangos deben tener al menos un registro.
     * Cubre el paso "Verificar completitud de los datos" del proceso.
     */
    public boolean isDataComplete(Map<String, Long> normalStatusMap,
                                  Map<String, Long> categoriaMap,
                                  Map<String, Long> focoMap,
                                  Map<String, Long> institucionMap,
                      Map<String, Long> monthMap,
                      Map<String, Long> valvulopathyAgeMap,
                      Map<String, Long> valvulopathyGenderMap,
                      Map<String, Long> valvulopathyDiseaseMap) {
        return !normalStatusMap.isEmpty()
                && !categoriaMap.isEmpty()
                && !focoMap.isEmpty()
                && !institucionMap.isEmpty()
            && !monthMap.isEmpty()
            && !valvulopathyAgeMap.isEmpty()
            && !valvulopathyGenderMap.isEmpty()
            && !valvulopathyDiseaseMap.isEmpty();
    }

    private Map<String, Long> toMap(List<Object[]> rows) {
        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : rows) {
            result.put(String.valueOf(row[0]), ((Number) row[1]).longValue());
        }
        return result;
    }
}