package com.tuapp.presentation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/anomalias")
public class AnomaliaApiController {

    private final CategoriaAnomaliaApiController categoriaAnomaliaApiController;

    public AnomaliaApiController(CategoriaAnomaliaApiController categoriaAnomaliaApiController) {
        this.categoriaAnomaliaApiController = categoriaAnomaliaApiController;
    }

    @GetMapping
    public List<Map<String, Object>> list() {
        return categoriaAnomaliaApiController.list();
    }
}
