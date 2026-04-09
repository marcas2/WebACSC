package com.tuapp.presentation.controller;

import com.tuapp.infrastructure.persistence.entity.FocoEntity;
import com.tuapp.infrastructure.persistence.repository.FocoJpaRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/focos")
public class FocoApiController {

    private final FocoJpaRepository focoRepository;

    public FocoApiController(FocoJpaRepository focoRepository) {
        this.focoRepository = focoRepository;
    }

    @GetMapping
    public List<Map<String, Object>> list() {
        return focoRepository.findAllByOrderByNombreAsc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private Map<String, Object> toResponse(FocoEntity foco) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", foco.getId());
        response.put("nombre", foco.getNombre());
        response.put("codigo", foco.getCodigo());
        return response;
    }
}
