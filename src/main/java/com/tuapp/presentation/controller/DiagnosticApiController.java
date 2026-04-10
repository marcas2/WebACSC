package com.tuapp.presentation.controller;

import com.tuapp.application.usecase.ConfirmValvulopatiaUseCase;
import com.tuapp.application.usecase.CreateDiagnosticUseCase;
import com.tuapp.application.usecase.GetDiagnosticsByCreatorUseCase;
import com.tuapp.infrastructure.persistence.entity.DiagnosticEntity;
import com.tuapp.presentation.dto.ConfirmValvulopatiaRequest;
import com.tuapp.presentation.dto.CreateDiagnosticRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/diagnostics")
public class DiagnosticApiController {

    private final CreateDiagnosticUseCase createDiagnosticUseCase;
    private final GetDiagnosticsByCreatorUseCase getDiagnosticsByCreatorUseCase;
    private final ConfirmValvulopatiaUseCase confirmValvulopatiaUseCase;

    public DiagnosticApiController(CreateDiagnosticUseCase createDiagnosticUseCase,
                                   GetDiagnosticsByCreatorUseCase getDiagnosticsByCreatorUseCase,
                                   ConfirmValvulopatiaUseCase confirmValvulopatiaUseCase) {
        this.createDiagnosticUseCase = createDiagnosticUseCase;
        this.getDiagnosticsByCreatorUseCase = getDiagnosticsByCreatorUseCase;
        this.confirmValvulopatiaUseCase = confirmValvulopatiaUseCase;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(
            @Valid @RequestBody CreateDiagnosticRequest request) {

        DiagnosticEntity saved = createDiagnosticUseCase.execute(request);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Diagnóstico creado correctamente");
        response.put("id", saved.getId());
        response.put("creadoEn", saved.getCreadoEn());
        response.put("institucionId", saved.getInstitucion().getId());
        response.put("institucionNombre", saved.getInstitucion().getNombre());
        response.put("esNormal", saved.getEsNormal());
        response.put("edad", saved.getEdad());
        response.put("genero", saved.getGenero());
        response.put("altura", saved.getAltura());
        response.put("peso", saved.getPeso());
        response.put("diagnosticoTexto", saved.getDiagnosticoTexto());
        response.put("verificado", saved.getVerificado());
        response.put("valvulopatia", saved.getValvulopatia());
        response.put("usuarioCreaId", saved.getUsuarioCrea() != null ? saved.getUsuarioCrea().getId() : null);
        response.put("focoId", saved.getFoco().getId());
        response.put("focoNombre", saved.getFoco().getNombre());
        response.put("categoriaAnomaliaId", saved.getCategoriaAnomalia().getId());
        response.put("categoriaAnomaliaNombre", saved.getCategoriaAnomalia().getNombre());

        List<Long> enfermedadesBaseIds = saved.getEnfermedadesBase().stream()
            .map(enfermedad -> enfermedad.getId())
            .collect(Collectors.toList());
        response.put("enfermedadesBaseIds", enfermedadesBaseIds);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/by-creator")
    public ResponseEntity<Map<String, Object>> getByCreator(
            @RequestParam(name = "usuarioCreaId", required = false) Long usuarioCreaId) {
        List<DiagnosticEntity> diagnostics = getDiagnosticsByCreatorUseCase.execute(usuarioCreaId);

        Map<String, List<DiagnosticEntity>> grouped = diagnostics.stream()
                .collect(Collectors.groupingBy(this::creatorGroupKey, LinkedHashMap::new, Collectors.toList()));

        List<Map<String, Object>> result = new ArrayList<>();
        grouped.forEach((groupKey, items) -> {
            DiagnosticEntity first = items.get(0);
            Map<String, Object> group = new LinkedHashMap<>();
            group.put("usuarioCreadorId", first.getUsuarioCrea() != null ? first.getUsuarioCrea().getId() : null);
            group.put("nombreUsuario", first.getUsuarioCrea() != null ? first.getUsuarioCrea().getNombreUsuario() : "SIN_USUARIO");
            group.put("totalDiagnosticos", items.size());
            group.put("diagnosticos", items.stream().map(this::toDiagnosticSummary).collect(Collectors.toList()));
            result.add(group);
        });

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Diagnósticos agrupados por usuario creador");
        response.put("filtroUsuarioCreaId", usuarioCreaId);
        response.put("data", result);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/confirm-valvulopatia")
    public ResponseEntity<Map<String, Object>> confirmValvulopatia(
            @PathVariable Long id,
            @Valid @RequestBody ConfirmValvulopatiaRequest request) {

        DiagnosticEntity updated = confirmValvulopatiaUseCase.execute(id, request.getValvulopatia());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Diagnóstico verificado correctamente");
        response.put("id", updated.getId());
        response.put("verificado", updated.getVerificado());
        response.put("valvulopatia", updated.getValvulopatia());
        response.put("usuarioCreaId", updated.getUsuarioCrea() != null ? updated.getUsuarioCrea().getId() : null);
        response.put("creadoEn", updated.getCreadoEn());

        return ResponseEntity.ok(response);
    }

    private String creatorGroupKey(DiagnosticEntity diagnostic) {
        if (diagnostic.getUsuarioCrea() == null) {
            return "SIN_USUARIO";
        }
        return diagnostic.getUsuarioCrea().getId() + "::" + Objects.toString(diagnostic.getUsuarioCrea().getNombreUsuario(), "");
    }

    private Map<String, Object> toDiagnosticSummary(DiagnosticEntity diagnostic) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", diagnostic.getId());
        item.put("creadoEn", diagnostic.getCreadoEn());
        item.put("institucionId", diagnostic.getInstitucion().getId());
        item.put("institucionNombre", diagnostic.getInstitucion().getNombre());
        item.put("esNormal", diagnostic.getEsNormal());
        item.put("verificado", diagnostic.getVerificado());
        item.put("valvulopatia", diagnostic.getValvulopatia());
        item.put("edad", diagnostic.getEdad());
        item.put("genero", diagnostic.getGenero());
        item.put("focoId", diagnostic.getFoco().getId());
        item.put("focoNombre", diagnostic.getFoco().getNombre());
        item.put("categoriaAnomaliaId", diagnostic.getCategoriaAnomalia().getId());
        item.put("categoriaAnomaliaNombre", diagnostic.getCategoriaAnomalia().getNombre());
        return item;
    }
}