package com.tuapp.application.service;

import com.tuapp.infrastructure.persistence.entity.CategoriaAnomaliaEntity;
import com.tuapp.infrastructure.persistence.entity.DiagnosticEntity;
import com.tuapp.infrastructure.persistence.entity.FocoEntity;
import com.tuapp.infrastructure.persistence.entity.InstitucionEntity;
import com.tuapp.infrastructure.persistence.repository.CategoriaAnomaliaJpaRepository;
import com.tuapp.infrastructure.persistence.repository.DiagnosticJpaRepository;
import com.tuapp.infrastructure.persistence.repository.FocoJpaRepository;
import com.tuapp.infrastructure.persistence.repository.InstitucionJpaRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class DashboardDiagnosticService {

    private final DiagnosticJpaRepository diagnosticRepository;
    private final InstitucionJpaRepository institucionRepository;
    private final FocoJpaRepository focoRepository;
    private final CategoriaAnomaliaJpaRepository categoriaAnomaliaRepository;

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    public DashboardDiagnosticService(DiagnosticJpaRepository diagnosticRepository,
                                      InstitucionJpaRepository institucionRepository,
                                      FocoJpaRepository focoRepository,
                                      CategoriaAnomaliaJpaRepository categoriaAnomaliaRepository) {
        this.diagnosticRepository = diagnosticRepository;
        this.institucionRepository = institucionRepository;
        this.focoRepository = focoRepository;
        this.categoriaAnomaliaRepository = categoriaAnomaliaRepository;
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

    public DashboardReportData getDashboardReportData(DashboardQueryFilters filters) {
        DashboardQueryFilters safeFilters = filters != null ? filters : DashboardQueryFilters.empty();
        List<DiagnosticEntity> diagnostics = getFilteredDiagnostics(safeFilters);

        Map<String, Long> normalStatusMap = countByNormalStatus(diagnostics);
        Map<String, Long> categoriaMap = countByCategoria(diagnostics);
        Map<String, Long> focoMap = countByFoco(diagnostics);
        Map<String, Long> institucionMap = countByInstitucion(diagnostics);
        Map<String, Long> monthMap = countByMonth(diagnostics);
        Map<String, Long> valvulopathyAgeMap = countValvulopathiesByAgeRange(diagnostics);
        Map<String, Long> valvulopathyGenderMap = countValvulopathiesByGender(diagnostics);
        Map<String, Long> valvulopathyDiseaseMap = countValvulopathiesByUnderlyingDiseases(diagnostics);

        return new DashboardReportData(
                diagnostics.size(),
                normalStatusMap,
                categoriaMap,
                focoMap,
                institucionMap,
                monthMap,
                valvulopathyAgeMap,
                valvulopathyGenderMap,
                valvulopathyDiseaseMap,
                diagnostics.isEmpty());
    }

    public DashboardFilterOptions getFilterOptions() {
        Map<Long, String> instituciones = institucionRepository.findAllByOrderByNombreAsc().stream()
                .collect(Collectors.toMap(
                        InstitucionEntity::getId,
                        InstitucionEntity::getNombre,
                        (left, right) -> left,
                        LinkedHashMap::new));

        Map<Long, String> focos = focoRepository.findAllByOrderByNombreAsc().stream()
                .collect(Collectors.toMap(
                        FocoEntity::getId,
                        FocoEntity::getNombre,
                        (left, right) -> left,
                        LinkedHashMap::new));

        Map<Long, String> categorias = categoriaAnomaliaRepository.findAllByOrderByNombreAsc().stream()
                .collect(Collectors.toMap(
                        CategoriaAnomaliaEntity::getId,
                        CategoriaAnomaliaEntity::getNombre,
                        (left, right) -> left,
                        LinkedHashMap::new));

        List<String> generos = diagnosticRepository.findDistinctGeneros();

        return new DashboardFilterOptions(instituciones, focos, categorias, generos);
    }

    private List<DiagnosticEntity> getFilteredDiagnostics(DashboardQueryFilters filters) {
        List<DiagnosticEntity> diagnostics = diagnosticRepository.findAllForDashboardFilters();

        Long institucionId = filters.getInstitucionId();
        if (institucionId != null) {
            diagnostics = diagnostics.stream()
                .filter(diagnostic -> diagnostic.getInstitucion() != null
                    && institucionId.equals(diagnostic.getInstitucion().getId()))
                .collect(Collectors.toList());
        }

        Long focoId = filters.getFocoId();
        if (focoId != null) {
            diagnostics = diagnostics.stream()
                .filter(diagnostic -> diagnostic.getFoco() != null
                    && focoId.equals(diagnostic.getFoco().getId()))
                .collect(Collectors.toList());
        }

        Long categoriaId = filters.getCategoriaId();
        if (categoriaId != null) {
            diagnostics = diagnostics.stream()
                .filter(diagnostic -> diagnostic.getCategoriaAnomalia() != null
                    && categoriaId.equals(diagnostic.getCategoriaAnomalia().getId()))
                .collect(Collectors.toList());
        }

        Integer edadMin = filters.getEdadMin();
        if (edadMin != null) {
            diagnostics = diagnostics.stream()
                .filter(diagnostic -> diagnostic.getEdad() != null && diagnostic.getEdad() >= edadMin)
                .collect(Collectors.toList());
        }

        Integer edadMax = filters.getEdadMax();
        if (edadMax != null) {
            diagnostics = diagnostics.stream()
                .filter(diagnostic -> diagnostic.getEdad() != null && diagnostic.getEdad() <= edadMax)
                .collect(Collectors.toList());
        }

        var desde = filters.getDesdeAtStartOfDay();
        if (desde != null) {
            diagnostics = diagnostics.stream()
                .filter(diagnostic -> diagnostic.getCreadoEn() != null
                    && !diagnostic.getCreadoEn().isBefore(desde))
                .collect(Collectors.toList());
        }

        var hastaExclusive = filters.getHastaExclusive();
        if (hastaExclusive != null) {
            diagnostics = diagnostics.stream()
                .filter(diagnostic -> diagnostic.getCreadoEn() != null
                    && diagnostic.getCreadoEn().isBefore(hastaExclusive))
                .collect(Collectors.toList());
        }

        String searchText = filters.getSearchText();
        if (searchText != null) {
            String expected = searchText.toLowerCase();
            diagnostics = diagnostics.stream()
                    .filter(diagnostic -> {
                        String diagnosticoTexto = diagnostic.getDiagnosticoTexto();
                        return diagnosticoTexto != null && diagnosticoTexto.toLowerCase().contains(expected);
                    })
                    .collect(Collectors.toList());
        }

        String genero = filters.getGenero();
        if (genero != null) {
            String expectedGender = normalizeGender(genero);
            diagnostics = diagnostics.stream()
                    .filter(diagnostic -> normalizeGender(diagnostic.getGenero()).equals(expectedGender))
                    .collect(Collectors.toList());
        }

        Boolean conEnfermedadBase = filters.getConEnfermedadBase();
        if (conEnfermedadBase != null) {
            diagnostics = diagnostics.stream()
                    .filter(diagnostic -> {
                        boolean hasDisease = diagnostic.getEnfermedadesBase() != null
                                && !diagnostic.getEnfermedadesBase().isEmpty();
                        return conEnfermedadBase ? hasDisease : !hasDisease;
                    })
                    .collect(Collectors.toList());
        }

        return diagnostics;
    }

    private Map<String, Long> countByNormalStatus(List<DiagnosticEntity> diagnostics) {
        Map<String, Long> result = new LinkedHashMap<>();
        result.put("NORMAL", 0L);
        result.put("ANORMAL", 0L);
        for (DiagnosticEntity diagnostic : diagnostics) {
            if (Boolean.TRUE.equals(diagnostic.getEsNormal())) {
                result.computeIfPresent("NORMAL", (k, v) -> v + 1);
            } else {
                result.computeIfPresent("ANORMAL", (k, v) -> v + 1);
            }
        }
        return result;
    }

    private Map<String, Long> countByCategoria(List<DiagnosticEntity> diagnostics) {
        return diagnostics.stream()
                .collect(Collectors.groupingBy(
                d -> d.getCategoriaAnomalia() != null
                    ? safeLabel(d.getCategoriaAnomalia().getNombre(), "SIN CATEGORIA")
                    : "SIN CATEGORIA",
                        TreeMap::new,
                        Collectors.counting()));
    }

    private Map<String, Long> countByFoco(List<DiagnosticEntity> diagnostics) {
        return diagnostics.stream()
                .collect(Collectors.groupingBy(
                d -> d.getFoco() != null
                    ? safeLabel(d.getFoco().getNombre(), "SIN FOCO")
                    : "SIN FOCO",
                        TreeMap::new,
                        Collectors.counting()));
    }

    private Map<String, Long> countByInstitucion(List<DiagnosticEntity> diagnostics) {
        return diagnostics.stream()
                .collect(Collectors.groupingBy(
                d -> d.getInstitucion() != null
                    ? safeLabel(d.getInstitucion().getNombre(), "SIN INSTITUCION")
                    : "SIN INSTITUCION",
                        TreeMap::new,
                        Collectors.counting()));
    }

    private Map<String, Long> countByMonth(List<DiagnosticEntity> diagnostics) {
        // 1. Contar registros sin fecha
        long noDateCount = diagnostics.stream()
            .map(DiagnosticEntity::getCreadoEn)
            .filter(Objects::isNull)
            .count();

        // 2. Agrupar los que sí tienen fecha, ordenados cronológicamente
        Map<String, Long> ordered = diagnostics.stream()
            .map(DiagnosticEntity::getCreadoEn)
            .filter(Objects::nonNull)
            .sorted(Comparator.naturalOrder())
            .collect(Collectors.groupingBy(
                createdAt -> MONTH_FORMATTER.format(createdAt),
                LinkedHashMap::new,
                Collectors.counting()));

        // 3. Agregar SIN_FECHA al final si aplica
        if (noDateCount > 0) {
            ordered.put("SIN_FECHA", noDateCount);
        }

        return ordered;
    }

    private Map<String, Long> countValvulopathiesByAgeRange(List<DiagnosticEntity> diagnostics) {
        Map<String, Long> result = new LinkedHashMap<>();
        result.put("0-17", 0L);
        result.put("18-35", 0L);
        result.put("36-59", 0L);
        result.put("60+", 0L);

        for (DiagnosticEntity diagnostic : diagnostics) {
            if (!isValvulopathyCase(diagnostic)) {
                continue;
            }
            String bucket = ageBucket(diagnostic.getEdad());
            result.computeIfPresent(bucket, (k, v) -> v + 1);
        }
        return result;
    }

    private Map<String, Long> countValvulopathiesByGender(List<DiagnosticEntity> diagnostics) {
        Map<String, Long> result = diagnostics.stream()
                .filter(this::isValvulopathyCase)
                .collect(Collectors.groupingBy(
                        d -> normalizeGender(d.getGenero()),
                        TreeMap::new,
                        Collectors.counting()));

        return new LinkedHashMap<>(result);
    }

    private Map<String, Long> countValvulopathiesByUnderlyingDiseases(List<DiagnosticEntity> diagnostics) {
        Map<String, Long> result = new LinkedHashMap<>();
        result.put("CON ENFERMEDAD DE BASE", 0L);
        result.put("SIN ENFERMEDAD DE BASE", 0L);

        for (DiagnosticEntity diagnostic : diagnostics) {
            if (!isValvulopathyCase(diagnostic)) {
                continue;
            }
            if (diagnostic.getEnfermedadesBase() == null || diagnostic.getEnfermedadesBase().isEmpty()) {
                result.computeIfPresent("SIN ENFERMEDAD DE BASE", (k, v) -> v + 1);
            } else {
                result.computeIfPresent("CON ENFERMEDAD DE BASE", (k, v) -> v + 1);
            }
        }
        return result;
    }

    private boolean isValvulopathyCase(DiagnosticEntity diagnostic) {
        return Boolean.FALSE.equals(diagnostic.getEsNormal()) || Boolean.TRUE.equals(diagnostic.getValvulopatia());
    }

    private String ageBucket(Integer age) {
        if (age == null || age < 18) {
            return "0-17";
        }
        if (age <= 35) {
            return "18-35";
        }
        if (age <= 59) {
            return "36-59";
        }
        return "60+";
    }

    private String normalizeGender(String genero) {
        if (genero == null) {
            return "SIN DATO";
        }
        String normalized = genero.trim().toUpperCase();
        return normalized.isEmpty() ? "SIN DATO" : normalized;
    }

    private String safeLabel(String value, String fallback) {
        if (value == null) {
            return fallback;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? fallback : normalized;
    }

    private Map<String, Long> toMap(List<Object[]> rows) {
        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : rows) {
            result.put(String.valueOf(row[0]), ((Number) row[1]).longValue());
        }
        return result;
    }

    public static class DashboardReportData {
        private final long totalRegistros;
        private final Map<String, Long> normalStatusMap;
        private final Map<String, Long> categoriaMap;
        private final Map<String, Long> focoMap;
        private final Map<String, Long> institucionMap;
        private final Map<String, Long> monthMap;
        private final Map<String, Long> valvulopathyAgeMap;
        private final Map<String, Long> valvulopathyGenderMap;
        private final Map<String, Long> valvulopathyDiseaseMap;
        private final boolean empty;

        public DashboardReportData(long totalRegistros,
                                   Map<String, Long> normalStatusMap,
                                   Map<String, Long> categoriaMap,
                                   Map<String, Long> focoMap,
                                   Map<String, Long> institucionMap,
                                   Map<String, Long> monthMap,
                                   Map<String, Long> valvulopathyAgeMap,
                                   Map<String, Long> valvulopathyGenderMap,
                                   Map<String, Long> valvulopathyDiseaseMap,
                                   boolean empty) {
            this.totalRegistros = totalRegistros;
            this.normalStatusMap = normalStatusMap;
            this.categoriaMap = categoriaMap;
            this.focoMap = focoMap;
            this.institucionMap = institucionMap;
            this.monthMap = monthMap;
            this.valvulopathyAgeMap = valvulopathyAgeMap;
            this.valvulopathyGenderMap = valvulopathyGenderMap;
            this.valvulopathyDiseaseMap = valvulopathyDiseaseMap;
            this.empty = empty;
        }

        public long getTotalRegistros() {
            return totalRegistros;
        }

        public Map<String, Long> getNormalStatusMap() {
            return normalStatusMap;
        }

        public Map<String, Long> getCategoriaMap() {
            return categoriaMap;
        }

        public Map<String, Long> getFocoMap() {
            return focoMap;
        }

        public Map<String, Long> getInstitucionMap() {
            return institucionMap;
        }

        public Map<String, Long> getMonthMap() {
            return monthMap;
        }

        public Map<String, Long> getValvulopathyAgeMap() {
            return valvulopathyAgeMap;
        }

        public Map<String, Long> getValvulopathyGenderMap() {
            return valvulopathyGenderMap;
        }

        public Map<String, Long> getValvulopathyDiseaseMap() {
            return valvulopathyDiseaseMap;
        }

        public boolean isEmpty() {
            return empty;
        }
    }

    public static class DashboardFilterOptions {
        private final Map<Long, String> instituciones;
        private final Map<Long, String> focos;
        private final Map<Long, String> categorias;
        private final List<String> generos;

        public DashboardFilterOptions(Map<Long, String> instituciones,
                                      Map<Long, String> focos,
                                      Map<Long, String> categorias,
                                      List<String> generos) {
            this.instituciones = instituciones;
            this.focos = focos;
            this.categorias = categorias;
            this.generos = generos;
        }

        public Map<Long, String> getInstituciones() {
            return instituciones;
        }

        public Map<Long, String> getFocos() {
            return focos;
        }

        public Map<Long, String> getCategorias() {
            return categorias;
        }

        public List<String> getGeneros() {
            return generos;
        }
    }
}