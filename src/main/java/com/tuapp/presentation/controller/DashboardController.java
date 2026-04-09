package com.tuapp.presentation.controller;

import com.tuapp.application.service.DashboardDiagnosticService;
import com.tuapp.util.ExcelExportUtil;
import com.tuapp.util.PdfExportUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class DashboardController {

    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    private final DashboardDiagnosticService dashboardDiagnosticService;

    public DashboardController(DashboardDiagnosticService dashboardDiagnosticService) {
        this.dashboardDiagnosticService = dashboardDiagnosticService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        model.addAttribute("username", resolveUsername(authentication));
        model.addAttribute("role", resolveRole(authentication));
        try {

        if (!dashboardDiagnosticService.hasData()) {
            model.addAttribute("infoMessage",
                "No se encontraron registros de sonidos cardíacos en el sistema.");
            populateEmptyDashboardModel(model);
            return "dashboard";
        }
            model.addAttribute("username", resolveUsername(authentication));
            model.addAttribute("role", resolveRole(authentication));

        Map<String, Long> normalStatusMap = ensureLabels(
            dashboardDiagnosticService.getByNormalStatus(),
            new String[]{"NORMAL", "ANORMAL"});
        Map<String, Long> categoriaMap = dashboardDiagnosticService.getByCategoriaAnomalia();
        Map<String, Long> focoMap = dashboardDiagnosticService.getByFoco();
        Map<String, Long> institucionMap = dashboardDiagnosticService.getByInstitucion();
        Map<String, Long> monthMap = dashboardDiagnosticService.getByMonth();
        Map<String, Long> valvulopathyAgeMap = dashboardDiagnosticService.getValvulopathiesByAgeRange();
        Map<String, Long> valvulopathyGenderMap = dashboardDiagnosticService.getValvulopathiesByGender();
        Map<String, Long> valvulopathyDiseaseMap = dashboardDiagnosticService.getValvulopathiesByUnderlyingDiseases();

        if (!dashboardDiagnosticService.isDataComplete(
            normalStatusMap, categoriaMap, focoMap, institucionMap, monthMap,
            valvulopathyAgeMap, valvulopathyGenderMap, valvulopathyDiseaseMap)) {
            model.addAttribute("errorMessage",
                "Los datos están incompletos para generar el reporte estadístico solicitado.");
            populateEmptyDashboardModel(model);
            return "dashboard";
        }

        model.addAttribute("normalStatusLabels", new ArrayList<>(normalStatusMap.keySet()));
        model.addAttribute("normalStatusData", new ArrayList<>(normalStatusMap.values()));
        model.addAttribute("anomaliaLabels", new ArrayList<>(categoriaMap.keySet()));
        model.addAttribute("anomaliaData", new ArrayList<>(categoriaMap.values()));
        model.addAttribute("focoLabels", new ArrayList<>(focoMap.keySet()));
        model.addAttribute("focoData", new ArrayList<>(focoMap.values()));
        model.addAttribute("institucionLabels", new ArrayList<>(institucionMap.keySet()));
        model.addAttribute("institucionData", new ArrayList<>(institucionMap.values()));
        model.addAttribute("timelineLabels", new ArrayList<>(monthMap.keySet()));
        model.addAttribute("timelineData", new ArrayList<>(monthMap.values()));
        model.addAttribute("valvAgeLabels", new ArrayList<>(valvulopathyAgeMap.keySet()));
        model.addAttribute("valvAgeData", new ArrayList<>(valvulopathyAgeMap.values()));
        model.addAttribute("valvGenderLabels", new ArrayList<>(valvulopathyGenderMap.keySet()));
        model.addAttribute("valvGenderData", new ArrayList<>(valvulopathyGenderMap.values()));
        model.addAttribute("valvDiseaseLabels", new ArrayList<>(valvulopathyDiseaseMap.keySet()));
        model.addAttribute("valvDiseaseData", new ArrayList<>(valvulopathyDiseaseMap.values()));

        long totalRegistros = dashboardDiagnosticService.getTotalRegistros();
        model.addAttribute("totalRegistros", totalRegistros);

        long normales = normalStatusMap.getOrDefault("NORMAL", 0L);
        long anormales = normalStatusMap.getOrDefault("ANORMAL", 0L);
        int anormalPct = totalRegistros > 0
            ? (int) Math.round(anormales * 100.0 / totalRegistros)
            : 0;

        String anomaliaTop = categoriaMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
            .orElse("Sin datos");

        model.addAttribute("normalPct", totalRegistros > 0
            ? (int) Math.round(normales * 100.0 / totalRegistros)
            : 0);
        model.addAttribute("anormalPct", anormalPct);
        model.addAttribute("anomaliaTop", anomaliaTop);

        model.addAttribute("reportReady", true);
        return "dashboard";
        } catch (Exception ex) {
            log.error("Error generando dashboard", ex);
            model.addAttribute("errorMessage",
                    "Ocurrió un error al generar el dashboard. Revisa los datos y vuelve a intentar.");
            model.addAttribute("username", resolveUsername(authentication));
            model.addAttribute("role", resolveRole(authentication));
            populateEmptyDashboardModel(model);
            return "dashboard";
        }
    }

    @GetMapping("/dashboard/export")
    public ResponseEntity<byte[]> export(
            @RequestParam String format,
            Authentication authentication) {
        try {

        Map<String, Long> normalStatusMap = ensureLabels(
                dashboardDiagnosticService.getByNormalStatus(),
                new String[]{"NORMAL", "ANORMAL"});
        Map<String, Long> categoriaMap = dashboardDiagnosticService.getByCategoriaAnomalia();
        Map<String, Long> focoMap = dashboardDiagnosticService.getByFoco();
        Map<String, Long> institucionMap = dashboardDiagnosticService.getByInstitucion();
        Map<String, Long> monthMap = dashboardDiagnosticService.getByMonth();
        Map<String, Long> valvulopathyAgeMap = dashboardDiagnosticService.getValvulopathiesByAgeRange();
        Map<String, Long> valvulopathyGenderMap = dashboardDiagnosticService.getValvulopathiesByGender();
        Map<String, Long> valvulopathyDiseaseMap = dashboardDiagnosticService.getValvulopathiesByUnderlyingDiseases();
        long totalRegistros = dashboardDiagnosticService.getTotalRegistros();

        byte[] file;
        String filename;
        String contentType;

        if ("excel".equalsIgnoreCase(format)) {
                file        = ExcelExportUtil.toExcel(totalRegistros, normalStatusMap, categoriaMap, focoMap, institucionMap,
                    monthMap, valvulopathyAgeMap, valvulopathyGenderMap, valvulopathyDiseaseMap);
            filename    = "reporte_sonidos_cardiacos.xlsx";
            contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        } else {
                file        = PdfExportUtil.toPdf(totalRegistros, normalStatusMap, categoriaMap, focoMap, institucionMap,
                    monthMap, valvulopathyAgeMap, valvulopathyGenderMap, valvulopathyDiseaseMap);
            filename    = "reporte_sonidos_cardiacos.pdf";
            contentType = MediaType.APPLICATION_PDF_VALUE;
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(file);
        } catch (Exception ex) {
            log.error("Error exportando dashboard", ex);
            return ResponseEntity.internalServerError()
                .contentType(MediaType.TEXT_PLAIN)
                .body("No se pudo generar el archivo de exportación.".getBytes());
        }
    }

    private Map<String, Long> ensureLabels(Map<String, Long> original, String[] expectedLabels) {
        Map<String, Long> normalized = new LinkedHashMap<>();
        for (String label : expectedLabels) {
            normalized.put(label, original.getOrDefault(label, 0L));
        }
        return normalized;
    }

    private void populateEmptyDashboardModel(Model model) {
        model.addAttribute("normalStatusLabels", new ArrayList<>(java.util.List.of("NORMAL", "ANORMAL")));
        model.addAttribute("normalStatusData", new ArrayList<>(java.util.List.of(0L, 0L)));
        model.addAttribute("anomaliaLabels", new ArrayList<>(java.util.List.of("Sin datos")));
        model.addAttribute("anomaliaData", new ArrayList<>(java.util.List.of(0L)));
        model.addAttribute("focoLabels", new ArrayList<>(java.util.List.of("Sin datos")));
        model.addAttribute("focoData", new ArrayList<>(java.util.List.of(0L)));
        model.addAttribute("institucionLabels", new ArrayList<>(java.util.List.of("Sin datos")));
        model.addAttribute("institucionData", new ArrayList<>(java.util.List.of(0L)));
        model.addAttribute("timelineLabels", new ArrayList<>(java.util.List.of("Sin datos")));
        model.addAttribute("timelineData", new ArrayList<>(java.util.List.of(0L)));
        model.addAttribute("valvAgeLabels", new ArrayList<>(java.util.List.of("0-17", "18-35", "36-59", "60+")));
        model.addAttribute("valvAgeData", new ArrayList<>(java.util.List.of(0L, 0L, 0L, 0L)));
        model.addAttribute("valvGenderLabels", new ArrayList<>(java.util.List.of("M", "F")));
        model.addAttribute("valvGenderData", new ArrayList<>(java.util.List.of(0L, 0L)));
        model.addAttribute("valvDiseaseLabels", new ArrayList<>(java.util.List.of("CON ENFERMEDAD DE BASE", "SIN ENFERMEDAD DE BASE")));
        model.addAttribute("valvDiseaseData", new ArrayList<>(java.util.List.of(0L, 0L)));

        model.addAttribute("totalRegistros", 0);
        model.addAttribute("normalPct", 0);
        model.addAttribute("anormalPct", 0);
        model.addAttribute("anomaliaTop", "Sin datos");
        model.addAttribute("reportReady", false);
    }

    private String resolveUsername(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            return "Invitado";
        }
        return authentication.getName();
    }

    private String resolveRole(Authentication authentication) {
        if (authentication == null || authentication.getAuthorities() == null) {
            return "SIN_ROL";
        }

        Optional<? extends GrantedAuthority> authority = authentication.getAuthorities().stream().findFirst();
        return authority.map(GrantedAuthority::getAuthority).orElse("SIN_ROL");
    }
}