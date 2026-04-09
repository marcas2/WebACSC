package com.tuapp.presentation.controller;

import com.tuapp.infrastructure.persistence.entity.EnfermedadBaseEntity;
import com.tuapp.infrastructure.persistence.repository.EnfermedadBaseJpaRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/api/enfermedades", "/api/enfermedades-base"})
public class EnfermedadBaseApiController {

    private final EnfermedadBaseJpaRepository enfermedadRepository;

    public EnfermedadBaseApiController(EnfermedadBaseJpaRepository enfermedadRepository) {
        this.enfermedadRepository = enfermedadRepository;
    }

    @GetMapping
    public List<Map<String, Object>> list() {
        return enfermedadRepository.findAllByOrderByNombreAsc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private Map<String, Object> toResponse(EnfermedadBaseEntity enfermedad) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", enfermedad.getId());
        response.put("nombre", enfermedad.getNombre());
        return response;
    }
}
