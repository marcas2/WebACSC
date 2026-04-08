package com.tuapp.presentation.controller;

import com.tuapp.application.usecase.CreateDiagnosticUseCase;
import com.tuapp.infrastructure.persistence.entity.DiagnosticEntity;
import com.tuapp.presentation.dto.CreateDiagnosticRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/diagnostics")
public class DiagnosticApiController {

    private final CreateDiagnosticUseCase createDiagnosticUseCase;

    public DiagnosticApiController(CreateDiagnosticUseCase createDiagnosticUseCase) {
        this.createDiagnosticUseCase = createDiagnosticUseCase;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(
            @Valid @RequestBody CreateDiagnosticRequest request) {

        DiagnosticEntity saved = createDiagnosticUseCase.execute(request);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Diagnóstico creado correctamente");
        response.put("id", saved.getId());
        response.put("createdAt", saved.getCreatedAt());
        response.put("institucion", saved.getInstitucion());
        response.put("isNormal", saved.getIsNormal());
        response.put("age", saved.getAge());
        response.put("gender", saved.getGender());
        response.put("altura", saved.getAltura());
        response.put("peso", saved.getPeso());
        response.put("diagnosticoTexto", saved.getDiagnosticoTexto());
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
}