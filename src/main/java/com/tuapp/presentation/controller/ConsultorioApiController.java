package com.tuapp.presentation.controller;

import com.tuapp.infrastructure.persistence.entity.ConsultorioEntity;
import com.tuapp.infrastructure.persistence.entity.InstitucionEntity;
import com.tuapp.infrastructure.persistence.repository.ConsultorioJpaRepository;
import com.tuapp.infrastructure.persistence.repository.InstitucionJpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/instituciones/{institucionId}/consultorios")
public class ConsultorioApiController {

    private final ConsultorioJpaRepository consultorioRepository;
    private final InstitucionJpaRepository institucionRepository;

    public ConsultorioApiController(ConsultorioJpaRepository consultorioRepository,
                                    InstitucionJpaRepository institucionRepository) {
        this.consultorioRepository = consultorioRepository;
        this.institucionRepository = institucionRepository;
    }

    @GetMapping
    public ResponseEntity<?> listByInstitucion(@PathVariable Long institucionId) {
        InstitucionEntity institucion = institucionRepository.findById(institucionId).orElse(null);
        if (institucion == null) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("message", "Institucion no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        List<Map<String, Object>> consultorios = consultorioRepository
                .findByInstitucionIdOrderByNombreAsc(institucionId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("institucion", Map.of(
                "id", institucion.getId(),
                "nombre", institucion.getNombre(),
                "activo", institucion.getActivo()
        ));
        response.put("consultorios", consultorios);

        return ResponseEntity.ok(response);
    }

    private Map<String, Object> toResponse(ConsultorioEntity consultorio) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", consultorio.getId());
        response.put("nombre", consultorio.getNombre());
        response.put("codigo", consultorio.getCodigo());
        response.put("activo", consultorio.getActivo());
        response.put("institucionId", consultorio.getInstitucion().getId());
        response.put("institucionNombre", consultorio.getInstitucion().getNombre());
        response.put("creadoEn", consultorio.getCreadoEn());
        return response;
    }
}
