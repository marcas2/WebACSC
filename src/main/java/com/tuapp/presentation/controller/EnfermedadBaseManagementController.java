package com.tuapp.presentation.controller;

import com.tuapp.infrastructure.persistence.entity.EnfermedadBaseEntity;
import com.tuapp.infrastructure.persistence.repository.EnfermedadBaseJpaRepository;
import com.tuapp.presentation.dto.CreateEnfermedadBaseRequest;
import com.tuapp.presentation.dto.UpdateEnfermedadBaseRequest;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/dashboard/enfermedades")
public class EnfermedadBaseManagementController {

    private final EnfermedadBaseJpaRepository enfermedadRepository;

    public EnfermedadBaseManagementController(EnfermedadBaseJpaRepository enfermedadRepository) {
        this.enfermedadRepository = enfermedadRepository;
    }

    @GetMapping
    public String list(Model model,
                       @RequestParam(required = false) String success,
                       @RequestParam(required = false) String error) {
        model.addAttribute("enfermedades", enfermedadRepository.findAllByOrderByNombreAsc());
        model.addAttribute("success", success);
        model.addAttribute("error", error);
        return "enfermedad-management";
    }

    @PostMapping("/create")
    public String create(@Valid CreateEnfermedadBaseRequest request) {
        try {
            String nombre = normalize(request.getNombre());
            if (enfermedadRepository.existsByNombreIgnoreCase(nombre)) {
                throw new IllegalArgumentException("Ya existe una enfermedad con ese nombre");
            }

            EnfermedadBaseEntity entity = new EnfermedadBaseEntity();
            entity.setNombre(nombre);
            enfermedadRepository.save(entity);
            return "redirect:/dashboard/enfermedades?success=Enfermedad creada correctamente";
        } catch (IllegalArgumentException ex) {
            return "redirect:/dashboard/enfermedades?error=" + ex.getMessage();
        }
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id, @Valid UpdateEnfermedadBaseRequest request) {
        try {
            EnfermedadBaseEntity entity = enfermedadRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Enfermedad no encontrada"));

            String nombre = normalize(request.getNombre());
            if (enfermedadRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
                throw new IllegalArgumentException("Ya existe una enfermedad con ese nombre");
            }

            entity.setNombre(nombre);
            enfermedadRepository.save(entity);
            return "redirect:/dashboard/enfermedades?success=Enfermedad actualizada correctamente";
        } catch (IllegalArgumentException ex) {
            return "redirect:/dashboard/enfermedades?error=" + ex.getMessage();
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        try {
            if (!enfermedadRepository.existsById(id)) {
                throw new IllegalArgumentException("Enfermedad no encontrada");
            }
            enfermedadRepository.deleteById(id);
            return "redirect:/dashboard/enfermedades?success=Enfermedad eliminada correctamente";
        } catch (DataIntegrityViolationException ex) {
            return "redirect:/dashboard/enfermedades?error=No se puede eliminar la enfermedad porque esta en uso";
        } catch (IllegalArgumentException ex) {
            return "redirect:/dashboard/enfermedades?error=" + ex.getMessage();
        }
    }

    private String normalize(String value) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        return normalized;
    }
}
