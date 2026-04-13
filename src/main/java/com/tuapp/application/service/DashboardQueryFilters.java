package com.tuapp.application.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DashboardQueryFilters {

    private final String searchText;
    private final Long institucionId;
    private final Long focoId;
    private final Long categoriaId;
    private final String genero;
    private final Integer edadMin;
    private final Integer edadMax;
    private final LocalDate desde;
    private final LocalDate hasta;
    private final Boolean conEnfermedadBase;

    public DashboardQueryFilters(String searchText,
                                 Long institucionId,
                                 Long focoId,
                                 Long categoriaId,
                                 String genero,
                                 Integer edadMin,
                                 Integer edadMax,
                                 LocalDate desde,
                                 LocalDate hasta,
                                 Boolean conEnfermedadBase) {
        this.searchText = normalizeText(searchText);
        this.institucionId = institucionId;
        this.focoId = focoId;
        this.categoriaId = categoriaId;
        this.genero = normalizeText(genero);
        this.edadMin = edadMin;
        this.edadMax = edadMax;
        this.desde = desde;
        this.hasta = hasta;
        this.conEnfermedadBase = conEnfermedadBase;
    }

    public static DashboardQueryFilters empty() {
        return new DashboardQueryFilters(null, null, null, null, null, null, null, null, null, null);
    }

    public String getSearchText() {
        return searchText;
    }

    public Long getInstitucionId() {
        return institucionId;
    }

    public Long getFocoId() {
        return focoId;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public String getGenero() {
        return genero;
    }

    public Integer getEdadMin() {
        return edadMin;
    }

    public Integer getEdadMax() {
        return edadMax;
    }

    public LocalDate getDesde() {
        return desde;
    }

    public LocalDate getHasta() {
        return hasta;
    }

    public Boolean getConEnfermedadBase() {
        return conEnfermedadBase;
    }

    public LocalDateTime getDesdeAtStartOfDay() {
        return desde != null ? desde.atStartOfDay() : null;
    }

    public LocalDateTime getHastaExclusive() {
        return hasta != null ? hasta.plusDays(1).atStartOfDay() : null;
    }

    public boolean hasAnyFilter() {
        return searchText != null
                || institucionId != null
                || focoId != null
                || categoriaId != null
                || genero != null
                || edadMin != null
                || edadMax != null
                || desde != null
                || hasta != null
                || conEnfermedadBase != null;
    }

    private String normalizeText(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
