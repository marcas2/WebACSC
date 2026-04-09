package com.tuapp.presentation.controller;

import com.tuapp.infrastructure.persistence.entity.InstitucionEntity;
import com.tuapp.infrastructure.persistence.repository.InstitucionJpaRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/instituciones")
public class InstitucionApiController {

    private final InstitucionJpaRepository institucionRepository;

    public InstitucionApiController(InstitucionJpaRepository institucionRepository) {
        this.institucionRepository = institucionRepository;
    }

    @GetMapping
    public List<Map<String, Object>> list() {
        return institucionRepository.findAllByOrderByNombreAsc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private Map<String, Object> toResponse(InstitucionEntity institucion) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", institucion.getId());
        response.put("nombre", institucion.getNombre());
        response.put("activo", institucion.getActivo());
        response.put("creadoEn", institucion.getCreadoEn());
        return response;
    }
}
