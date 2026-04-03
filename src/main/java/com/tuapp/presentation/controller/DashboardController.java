package com.tuapp.presentation.controller;

import com.tuapp.application.service.DashboardDiagnosticService;
import com.tuapp.util.ExcelExportUtil;
import com.tuapp.util.PdfExportUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Map;

@Controller
public class DashboardController {

    private final DashboardDiagnosticService dashboardDiagnosticService;

    public DashboardController(DashboardDiagnosticService dashboardDiagnosticService) {
        this.dashboardDiagnosticService = dashboardDiagnosticService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {

        model.addAttribute("username", authentication.getName());
        model.addAttribute("role",
                authentication.getAuthorities().iterator().next().getAuthority());

        if (!dashboardDiagnosticService.hasData()) {
            model.addAttribute("infoMessage",
                    "No se encontraron registros de valvulopatías en el sistema.");
            return "dashboard";
        }

        Map<String, Long> ageMap     = dashboardDiagnosticService.getByAgeRange();
        Map<String, Long> genderMap  = dashboardDiagnosticService.getByGender();
        Map<String, Long> diseaseMap = dashboardDiagnosticService.getByUnderlyingDiseases();

        if (!dashboardDiagnosticService.hasData()) {
    model.addAttribute("infoMessage",
            "No se encontraron registros de valvulopatías en el sistema.");

    model.addAttribute("ageLabels", new ArrayList<>());
    model.addAttribute("ageData", new ArrayList<>());
    model.addAttribute("genderLabels", new ArrayList<>());
    model.addAttribute("genderData", new ArrayList<>());
    model.addAttribute("diseaseLabels", new ArrayList<>());
    model.addAttribute("diseaseData", new ArrayList<>());

    model.addAttribute("totalValvulopathies", 0);
    model.addAttribute("topAgeRange", "Sin datos");
    model.addAttribute("diseaseBasePercent", 0);
    model.addAttribute("reportReady", false);

    return "dashboard";
}

        model.addAttribute("ageLabels",     new ArrayList<>(ageMap.keySet()));
        model.addAttribute("ageData",       new ArrayList<>(ageMap.values()));
        model.addAttribute("genderLabels",  new ArrayList<>(genderMap.keySet()));
        model.addAttribute("genderData",    new ArrayList<>(genderMap.values()));
        model.addAttribute("diseaseLabels", new ArrayList<>(diseaseMap.keySet()));
        model.addAttribute("diseaseData",   new ArrayList<>(diseaseMap.values()));

        long total = ageMap.values().stream().mapToLong(Long::longValue).sum();
        model.addAttribute("totalValvulopathies", total);

        String topAgeRange = ageMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("—");
        model.addAttribute("topAgeRange", topAgeRange);

        long conEnfermedad = diseaseMap.getOrDefault("CON ENFERMEDAD DE BASE", 0L);
        long totalDisease  = diseaseMap.values().stream().mapToLong(Long::longValue).sum();
        int pct = totalDisease > 0
                ? (int) Math.round(conEnfermedad * 100.0 / totalDisease) : 0;
        model.addAttribute("diseaseBasePercent", pct);

        model.addAttribute("reportReady", true);
        return "dashboard";
    }

    @GetMapping("/dashboard/export")
    public ResponseEntity<byte[]> export(
            @RequestParam String format,
            Authentication authentication) {

        Map<String, Long> ageMap     = dashboardDiagnosticService.getByAgeRange();
        Map<String, Long> genderMap  = dashboardDiagnosticService.getByGender();
        Map<String, Long> diseaseMap = dashboardDiagnosticService.getByUnderlyingDiseases();

        byte[] file;
        String filename;
        String contentType;

        if ("excel".equalsIgnoreCase(format)) {
            file        = ExcelExportUtil.toExcel(ageMap, genderMap, diseaseMap);
            filename    = "reporte_valvulopatias.xlsx";
            contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        } else {
            file        = PdfExportUtil.toPdf(ageMap, genderMap, diseaseMap);
            filename    = "reporte_valvulopatias.pdf";
            contentType = MediaType.APPLICATION_PDF_VALUE;
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(file);
    }
}