package com.tuapp.presentation.controller;

import com.tuapp.infrastructure.persistence.entity.CategoriaAnomaliaEntity;
import com.tuapp.infrastructure.persistence.repository.CategoriaAnomaliaJpaRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/api/categorias-anomalias", "/api/categorias"})
public class CategoriaAnomaliaApiController {

    private final CategoriaAnomaliaJpaRepository categoriaRepository;

    public CategoriaAnomaliaApiController(CategoriaAnomaliaJpaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @GetMapping
    public List<Map<String, Object>> list() {
        return categoriaRepository.findAllByOrderByNombreAsc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private Map<String, Object> toResponse(CategoriaAnomaliaEntity categoria) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", categoria.getId());
        response.put("nombre", categoria.getNombre());
        response.put("codigo", categoria.getCodigo());
        return response;
    }
}
