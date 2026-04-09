package com.tuapp.presentation.controller;

import com.tuapp.infrastructure.persistence.entity.InstitucionEntity;
import com.tuapp.infrastructure.persistence.repository.InstitucionJpaRepository;
import com.tuapp.presentation.dto.CreateInstitucionRequest;
import com.tuapp.presentation.dto.UpdateInstitucionRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/dashboard/instituciones")
public class InstitucionManagementController {

    private final InstitucionJpaRepository institucionRepository;

    public InstitucionManagementController(InstitucionJpaRepository institucionRepository) {
        this.institucionRepository = institucionRepository;
    }

    @GetMapping
    public String list(Model model,
                       @RequestParam(required = false) String success,
                       @RequestParam(required = false) String error) {
        model.addAttribute("instituciones", institucionRepository.findAllByOrderByNombreAsc());
        model.addAttribute("success", success);
        model.addAttribute("error", error);
        return "institution-management";
    }

    @PostMapping("/create")
    public String create(@Valid CreateInstitucionRequest request) {
        try {
            String nombre = normalizeText(request.getNombre());
            if (institucionRepository.existsByNombreIgnoreCase(nombre)) {
                throw new IllegalArgumentException("Ya existe una institucion con ese nombre");
            }

            InstitucionEntity entity = new InstitucionEntity();
            entity.setNombre(nombre);
            entity.setActivo(true);
            institucionRepository.save(entity);
            return "redirect:/dashboard/instituciones?success=Institucion creada correctamente";
        } catch (IllegalArgumentException ex) {
            return "redirect:/dashboard/instituciones?error=" + ex.getMessage();
        }
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id, @Valid UpdateInstitucionRequest request) {
        try {
            InstitucionEntity entity = institucionRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Institucion no encontrada"));

            String nombre = normalizeText(request.getNombre());
            if (institucionRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
                throw new IllegalArgumentException("Ya existe una institucion con ese nombre");
            }

            entity.setNombre(nombre);
            entity.setActivo(request.getActivo());
            institucionRepository.save(entity);
            return "redirect:/dashboard/instituciones?success=Institucion actualizada correctamente";
        } catch (IllegalArgumentException ex) {
            return "redirect:/dashboard/instituciones?error=" + ex.getMessage();
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        try {
            if (!institucionRepository.existsById(id)) {
                throw new IllegalArgumentException("Institucion no encontrada");
            }
            institucionRepository.deleteById(id);
            return "redirect:/dashboard/instituciones?success=Institucion eliminada correctamente";
        } catch (IllegalArgumentException ex) {
            return "redirect:/dashboard/instituciones?error=" + ex.getMessage();
        }
    }

    private String normalizeText(String value) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        return normalized;
    }
}
