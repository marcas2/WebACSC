package com.tuapp.presentation.controller;

import com.tuapp.application.service.DashboardDiagnosticService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    private DashboardDiagnosticService dashboardDiagnosticService;

    private DashboardController controller;

    @BeforeEach
    void setUp() {
        controller = new DashboardController(dashboardDiagnosticService);
    }

    @Test
    void shouldRenderDashboardWithDataWhenAccessingModule() {
        mockCompleteDashboardData();
        Model model = new ExtendedModelMap();

        String view = controller.dashboard(null, model);

        assertEquals("dashboard", view);
        assertTrue((Boolean) model.getAttribute("reportReady"));
    }

    @Test
    void hu005_shouldShowValvulopathiesByAgeRangeCorrectly() {
        mockCompleteDashboardData();
        Model model = new ExtendedModelMap();

        controller.dashboard(null, model);

        assertEquals(List.of("0-17", "18-35", "36-59", "60+"), model.getAttribute("valvAgeLabels"));
        assertEquals(List.of(1L, 2L, 1L, 1L), model.getAttribute("valvAgeData"));
    }

    @Test
    void hu006_shouldShowValvulopathiesByGenderCorrectly() {
        mockCompleteDashboardData();
        Model model = new ExtendedModelMap();

        controller.dashboard(null, model);

        assertEquals(List.of("F", "M"), model.getAttribute("valvGenderLabels"));
        assertEquals(List.of(2L, 3L), model.getAttribute("valvGenderData"));
    }

    @Test
    void hu007_shouldShowUnderlyingDiseasesCorrectly() {
        mockCompleteDashboardData();
        Model model = new ExtendedModelMap();

        controller.dashboard(null, model);

        assertEquals(List.of("CON ENFERMEDAD DE BASE", "SIN ENFERMEDAD DE BASE"), model.getAttribute("valvDiseaseLabels"));
        assertEquals(List.of(4L, 1L), model.getAttribute("valvDiseaseData"));
    }

    @Test
    void hu010_shouldShowTotalRecordsCorrectly() {
        mockCompleteDashboardData();
        Model model = new ExtendedModelMap();

        controller.dashboard(null, model);

        assertEquals(5L, model.getAttribute("totalRegistros"));
    }

    @Test
    void hu011_shouldShowNormalAndAbnormalCountsCorrectly() {
        mockCompleteDashboardData();
        Model model = new ExtendedModelMap();

        controller.dashboard(null, model);

        assertEquals(List.of("NORMAL", "ANORMAL"), model.getAttribute("normalStatusLabels"));
        assertEquals(List.of(3L, 2L), model.getAttribute("normalStatusData"));
    }

    @Test
    void hu012_shouldShowRecordsByAnomalyTypeCorrectly() {
        mockCompleteDashboardData();
        Model model = new ExtendedModelMap();

        controller.dashboard(null, model);

        assertEquals(List.of("Estenosis", "Insuficiencia"), model.getAttribute("anomaliaLabels"));
        assertEquals(List.of(2L, 3L), model.getAttribute("anomaliaData"));
    }

    @Test
    void hu013_shouldShowRecordsByAuscultationFocusCorrectly() {
        mockCompleteDashboardData();
        Model model = new ExtendedModelMap();

        controller.dashboard(null, model);

        assertEquals(List.of("Aortico", "Mitral"), model.getAttribute("focoLabels"));
        assertEquals(List.of(2L, 3L), model.getAttribute("focoData"));
    }

    @Test
    void hu014_shouldShowRecordsByHospitalCorrectly() {
        mockCompleteDashboardData();
        Model model = new ExtendedModelMap();

        controller.dashboard(null, model);

        assertEquals(List.of("Hospital Norte", "Hospital Sur"), model.getAttribute("institucionLabels"));
        assertEquals(List.of(2L, 3L), model.getAttribute("institucionData"));
    }

    @Test
    void hu015_shouldShowTemporalEvolutionCorrectly() {
        mockCompleteDashboardData();
        Model model = new ExtendedModelMap();

        controller.dashboard(null, model);

        assertEquals(List.of("2026-01", "2026-02", "2026-03"), model.getAttribute("timelineLabels"));
        assertEquals(List.of(1L, 2L, 2L), model.getAttribute("timelineData"));
    }

    @Test
    void shouldShowZeroValuesWhenThereIsNoData() {
        when(dashboardDiagnosticService.hasData()).thenReturn(false);
        Model model = new ExtendedModelMap();

        String view = controller.dashboard(null, model);

        assertEquals("dashboard", view);
        assertFalse((Boolean) model.getAttribute("reportReady"));
        assertEquals(0, model.getAttribute("totalRegistros"));
        assertEquals(List.of(0L, 0L, 0L, 0L), model.getAttribute("valvAgeData"));
        assertEquals(List.of(0L, 0L), model.getAttribute("valvGenderData"));
        assertEquals(List.of(0L, 0L), model.getAttribute("valvDiseaseData"));
        assertEquals(List.of(0L, 0L), model.getAttribute("normalStatusData"));
        assertEquals(List.of(0L), model.getAttribute("anomaliaData"));
        assertEquals(List.of(0L), model.getAttribute("focoData"));
        assertEquals(List.of(0L), model.getAttribute("institucionData"));
        assertEquals(List.of(0L), model.getAttribute("timelineData"));
    }

    private void mockCompleteDashboardData() {
        when(dashboardDiagnosticService.hasData()).thenReturn(true);

        Map<String, Long> normalStatus = new LinkedHashMap<>();
        normalStatus.put("NORMAL", 3L);
        normalStatus.put("ANORMAL", 2L);

        Map<String, Long> categoria = new LinkedHashMap<>();
        categoria.put("Estenosis", 2L);
        categoria.put("Insuficiencia", 3L);

        Map<String, Long> foco = new LinkedHashMap<>();
        foco.put("Aortico", 2L);
        foco.put("Mitral", 3L);

        Map<String, Long> institucion = new LinkedHashMap<>();
        institucion.put("Hospital Norte", 2L);
        institucion.put("Hospital Sur", 3L);

        Map<String, Long> month = new LinkedHashMap<>();
        month.put("2026-01", 1L);
        month.put("2026-02", 2L);
        month.put("2026-03", 2L);

        Map<String, Long> valvAge = new LinkedHashMap<>();
        valvAge.put("0-17", 1L);
        valvAge.put("18-35", 2L);
        valvAge.put("36-59", 1L);
        valvAge.put("60+", 1L);

        Map<String, Long> valvGender = new LinkedHashMap<>();
        valvGender.put("F", 2L);
        valvGender.put("M", 3L);

        Map<String, Long> valvDisease = new LinkedHashMap<>();
        valvDisease.put("CON ENFERMEDAD DE BASE", 4L);
        valvDisease.put("SIN ENFERMEDAD DE BASE", 1L);

        when(dashboardDiagnosticService.getByNormalStatus()).thenReturn(normalStatus);
        when(dashboardDiagnosticService.getByCategoriaAnomalia()).thenReturn(categoria);
        when(dashboardDiagnosticService.getByFoco()).thenReturn(foco);
        when(dashboardDiagnosticService.getByInstitucion()).thenReturn(institucion);
        when(dashboardDiagnosticService.getByMonth()).thenReturn(month);
        when(dashboardDiagnosticService.getValvulopathiesByAgeRange()).thenReturn(valvAge);
        when(dashboardDiagnosticService.getValvulopathiesByGender()).thenReturn(valvGender);
        when(dashboardDiagnosticService.getValvulopathiesByUnderlyingDiseases()).thenReturn(valvDisease);

        when(dashboardDiagnosticService.isDataComplete(
                normalStatus,
                categoria,
                foco,
                institucion,
                month,
                valvAge,
                valvGender,
                valvDisease)).thenReturn(true);

        when(dashboardDiagnosticService.getTotalRegistros()).thenReturn(5L);
    }
}
