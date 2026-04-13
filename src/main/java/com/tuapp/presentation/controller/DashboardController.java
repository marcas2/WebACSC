package com.tuapp.presentation.controller;

import com.tuapp.application.service.DashboardDiagnosticService;
import com.tuapp.application.service.DashboardQueryFilters;
import com.tuapp.util.ExcelExportUtil;
import com.tuapp.util.PdfExportUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class DashboardController {

    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    private final DashboardDiagnosticService dashboardDiagnosticService;
    private final ResourceLoader resourceLoader;
    private final String apkResourcePath;
    private final String apkDownloadName;

    public DashboardController(DashboardDiagnosticService dashboardDiagnosticService,
                               ResourceLoader resourceLoader,
                               @Value("${app.apk.resource-path:classpath:static/apk/webacsc.apk}") String apkResourcePath,
                               @Value("${app.apk.download-name:webacsc.apk}") String apkDownloadName) {
        this.dashboardDiagnosticService = dashboardDiagnosticService;
        this.resourceLoader = resourceLoader;
        this.apkResourcePath = apkResourcePath;
        this.apkDownloadName = apkDownloadName;
    }

    @GetMapping("/dashboard")
    public String dashboardView(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long institucionId,
            @RequestParam(required = false) Long focoId,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) String genero,
            @RequestParam(required = false) Integer edadMin,
            @RequestParam(required = false) Integer edadMax,
            @RequestParam(required = false) LocalDate desde,
            @RequestParam(required = false) LocalDate hasta,
            @RequestParam(required = false) Boolean conEnfermedadBase,
            Authentication authentication,
            Model model) {

        DashboardQueryFilters filters = new DashboardQueryFilters(
                q,
                institucionId,
                focoId,
                categoriaId,
                genero,
                edadMin,
                edadMax,
                desde,
                hasta,
                conEnfermedadBase);

        return renderDashboard(authentication, model, filters);
    }

    public String dashboard(Authentication authentication, Model model) {
        return renderDashboard(authentication, model, DashboardQueryFilters.empty());
    }

    @GetMapping("/dashboard/export")
    public ResponseEntity<byte[]> exportWithFilters(
            @RequestParam String format,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long institucionId,
            @RequestParam(required = false) Long focoId,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) String genero,
            @RequestParam(required = false) Integer edadMin,
            @RequestParam(required = false) Integer edadMax,
            @RequestParam(required = false) LocalDate desde,
            @RequestParam(required = false) LocalDate hasta,
            @RequestParam(required = false) Boolean conEnfermedadBase,
            Authentication authentication) {

        DashboardQueryFilters filters = new DashboardQueryFilters(
                q,
                institucionId,
                focoId,
                categoriaId,
                genero,
                edadMin,
                edadMax,
                desde,
                hasta,
                conEnfermedadBase);

        return exportInternal(format, filters);
    }

    public ResponseEntity<byte[]> export(
            @RequestParam String format,
            Authentication authentication) {
        return exportInternal(format, DashboardQueryFilters.empty());
    }

    @GetMapping("/dashboard/apk/download")
    public ResponseEntity<byte[]> downloadApk() {
        try {
            Resource resource = resourceLoader.getResource(apkResourcePath);
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.status(404)
                        .contentType(MediaType.TEXT_PLAIN)
                        .body("No se encontro el archivo APK para descargar.".getBytes());
            }

            byte[] apkBytes = resource.getInputStream().readAllBytes();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + apkDownloadName + "\"")
                    .contentType(MediaType.parseMediaType("application/vnd.android.package-archive"))
                    .body(apkBytes);
        } catch (IOException ex) {
            log.error("Error descargando APK", ex);
            return ResponseEntity.internalServerError()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("No se pudo descargar el APK en este momento.".getBytes());
        }
    }

    private String renderDashboard(Authentication authentication, Model model, DashboardQueryFilters filters) {
        model.addAttribute("username", resolveUsername(authentication));
        model.addAttribute("role", resolveRole(authentication));
        try {
            bindFilterOptions(model, filters);
        } catch (Exception ex) {
            log.error("Error cargando opciones de filtros del dashboard", ex);
            model.addAttribute("institucionOptions", java.util.Collections.emptyMap());
            model.addAttribute("focoOptions", java.util.Collections.emptyMap());
            model.addAttribute("categoriaOptions", java.util.Collections.emptyMap());
            model.addAttribute("generoOptions", java.util.Collections.emptyList());
            model.addAttribute("filtroQ", filters.getSearchText());
            model.addAttribute("filtroInstitucionId", filters.getInstitucionId());
            model.addAttribute("filtroFocoId", filters.getFocoId());
            model.addAttribute("filtroCategoriaId", filters.getCategoriaId());
            model.addAttribute("filtroGenero", filters.getGenero());
            model.addAttribute("filtroEdadMin", filters.getEdadMin());
            model.addAttribute("filtroEdadMax", filters.getEdadMax());
            model.addAttribute("filtroDesde", filters.getDesde());
            model.addAttribute("filtroHasta", filters.getHasta());
            model.addAttribute("filtroConEnfermedadBase", filters.getConEnfermedadBase());
            model.addAttribute("hasActiveFilters", filters.hasAnyFilter());
        }
        try {
            if (filters == null || !filters.hasAnyFilter()) {
                return renderLegacyDashboard(model);
            }

            DashboardDiagnosticService.DashboardReportData reportData =
                    dashboardDiagnosticService.getDashboardReportData(filters);

            if (reportData.isEmpty()) {
                model.addAttribute("infoMessage",
                        "No hay resultados para los filtros seleccionados.");
                populateEmptyDashboardModel(model);
                return "dashboard";
            }

            Map<String, Long> normalStatusMap = ensureLabels(
                    reportData.getNormalStatusMap(),
                    new String[]{"NORMAL", "ANORMAL"});

            populateDashboardModel(
                    model,
                    reportData.getTotalRegistros(),
                    normalStatusMap,
                    reportData.getCategoriaMap(),
                    reportData.getFocoMap(),
                    reportData.getInstitucionMap(),
                    reportData.getMonthMap(),
                    reportData.getValvulopathyAgeMap(),
                    reportData.getValvulopathyGenderMap(),
                    reportData.getValvulopathyDiseaseMap());

            model.addAttribute("reportReady", true);
            return "dashboard";
        } catch (Exception ex) {
            log.error("Error generando dashboard", ex);
            try {
                model.addAttribute("errorMessage",
                        "Se presentó un inconveniente al aplicar filtros. Se muestra la vista general del dashboard.");
                return renderLegacyDashboard(model);
            } catch (Exception legacyEx) {
                log.error("Error generando dashboard en modo de respaldo", legacyEx);
                model.addAttribute("errorMessage",
                        "Ocurrió un error al generar el dashboard. Revisa los datos y vuelve a intentar.");
                populateEmptyDashboardModel(model);
                return "dashboard";
            }
        }
    }

    private String renderLegacyDashboard(Model model) {
        if (!dashboardDiagnosticService.hasData()) {
            model.addAttribute("infoMessage",
                    "No se encontraron registros de sonidos cardíacos en el sistema.");
            populateEmptyDashboardModel(model);
            return "dashboard";
        }

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
                normalStatusMap,
                categoriaMap,
                focoMap,
                institucionMap,
                monthMap,
                valvulopathyAgeMap,
                valvulopathyGenderMap,
                valvulopathyDiseaseMap)) {
            model.addAttribute("errorMessage",
                    "Los datos están incompletos para generar el reporte estadístico solicitado.");
            populateEmptyDashboardModel(model);
            return "dashboard";
        }

        long totalRegistros = dashboardDiagnosticService.getTotalRegistros();
        populateDashboardModel(model,
                totalRegistros,
                normalStatusMap,
                categoriaMap,
                focoMap,
                institucionMap,
                monthMap,
                valvulopathyAgeMap,
                valvulopathyGenderMap,
                valvulopathyDiseaseMap);
        model.addAttribute("reportReady", true);
        return "dashboard";
    }

    private ResponseEntity<byte[]> exportInternal(String format, DashboardQueryFilters filters) {
        try {
            Map<String, Long> normalStatusMap;
            Map<String, Long> categoriaMap;
            Map<String, Long> focoMap;
            Map<String, Long> institucionMap;
            Map<String, Long> monthMap;
            Map<String, Long> valvulopathyAgeMap;
            Map<String, Long> valvulopathyGenderMap;
            Map<String, Long> valvulopathyDiseaseMap;
            long totalRegistros;

            if (filters == null || !filters.hasAnyFilter()) {
                normalStatusMap = ensureLabels(
                        dashboardDiagnosticService.getByNormalStatus(),
                        new String[]{"NORMAL", "ANORMAL"});
                categoriaMap = dashboardDiagnosticService.getByCategoriaAnomalia();
                focoMap = dashboardDiagnosticService.getByFoco();
                institucionMap = dashboardDiagnosticService.getByInstitucion();
                monthMap = dashboardDiagnosticService.getByMonth();
                valvulopathyAgeMap = dashboardDiagnosticService.getValvulopathiesByAgeRange();
                valvulopathyGenderMap = dashboardDiagnosticService.getValvulopathiesByGender();
                valvulopathyDiseaseMap = dashboardDiagnosticService.getValvulopathiesByUnderlyingDiseases();
                totalRegistros = dashboardDiagnosticService.getTotalRegistros();
            } else {
                DashboardDiagnosticService.DashboardReportData reportData =
                        dashboardDiagnosticService.getDashboardReportData(filters);
                normalStatusMap = ensureLabels(reportData.getNormalStatusMap(), new String[]{"NORMAL", "ANORMAL"});
                categoriaMap = reportData.getCategoriaMap();
                focoMap = reportData.getFocoMap();
                institucionMap = reportData.getInstitucionMap();
                monthMap = reportData.getMonthMap();
                valvulopathyAgeMap = reportData.getValvulopathyAgeMap();
                valvulopathyGenderMap = reportData.getValvulopathyGenderMap();
                valvulopathyDiseaseMap = reportData.getValvulopathyDiseaseMap();
                totalRegistros = reportData.getTotalRegistros();
            }

            byte[] file;
            String filename;
            String contentType;

            if ("excel".equalsIgnoreCase(format)) {
                file = ExcelExportUtil.toExcel(totalRegistros, normalStatusMap, categoriaMap, focoMap, institucionMap,
                        monthMap, valvulopathyAgeMap, valvulopathyGenderMap, valvulopathyDiseaseMap);
                filename = "reporte_sonidos_cardiacos.xlsx";
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            } else {
                file = PdfExportUtil.toPdf(totalRegistros, normalStatusMap, categoriaMap, focoMap, institucionMap,
                        monthMap, valvulopathyAgeMap, valvulopathyGenderMap, valvulopathyDiseaseMap);
                filename = "reporte_sonidos_cardiacos.pdf";
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

    private void bindFilterOptions(Model model, DashboardQueryFilters filters) {
        DashboardDiagnosticService.DashboardFilterOptions options = dashboardDiagnosticService.getFilterOptions();
        model.addAttribute("institucionOptions", options.getInstituciones());
        model.addAttribute("focoOptions", options.getFocos());
        model.addAttribute("categoriaOptions", options.getCategorias());
        model.addAttribute("generoOptions", options.getGeneros());

        model.addAttribute("filtroQ", filters.getSearchText());
        model.addAttribute("filtroInstitucionId", filters.getInstitucionId());
        model.addAttribute("filtroFocoId", filters.getFocoId());
        model.addAttribute("filtroCategoriaId", filters.getCategoriaId());
        model.addAttribute("filtroGenero", filters.getGenero());
        model.addAttribute("filtroEdadMin", filters.getEdadMin());
        model.addAttribute("filtroEdadMax", filters.getEdadMax());
        model.addAttribute("filtroDesde", filters.getDesde());
        model.addAttribute("filtroHasta", filters.getHasta());
        model.addAttribute("filtroConEnfermedadBase", filters.getConEnfermedadBase());
        model.addAttribute("hasActiveFilters", filters.hasAnyFilter());
    }

    private void populateDashboardModel(Model model,
                                        long totalRegistros,
                                        Map<String, Long> normalStatusMap,
                                        Map<String, Long> categoriaMap,
                                        Map<String, Long> focoMap,
                                        Map<String, Long> institucionMap,
                                        Map<String, Long> monthMap,
                                        Map<String, Long> valvulopathyAgeMap,
                                        Map<String, Long> valvulopathyGenderMap,
                                        Map<String, Long> valvulopathyDiseaseMap) {
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