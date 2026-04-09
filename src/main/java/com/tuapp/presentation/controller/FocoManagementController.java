package com.tuapp.presentation.controller;

import com.tuapp.infrastructure.persistence.entity.FocoEntity;
import com.tuapp.infrastructure.persistence.repository.FocoJpaRepository;
import com.tuapp.presentation.dto.CreateFocoRequest;
import com.tuapp.presentation.dto.UpdateFocoRequest;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/dashboard/focos")
public class FocoManagementController {

    private final FocoJpaRepository focoRepository;

    public FocoManagementController(FocoJpaRepository focoRepository) {
        this.focoRepository = focoRepository;
    }

    @GetMapping
    public String list(Model model,
                       @RequestParam(required = false) String success,
                       @RequestParam(required = false) String error) {
        model.addAttribute("focos", focoRepository.findAllByOrderByNombreAsc());
        model.addAttribute("success", success);
        model.addAttribute("error", error);
        return "foco-management";
    }

    @PostMapping("/create")
    public String create(@Valid CreateFocoRequest request) {
        try {
            String nombre = normalize(request.getNombre(), "El nombre es obligatorio");
            String codigo = normalize(request.getCodigo(), "El codigo es obligatorio").toUpperCase();

            if (focoRepository.existsByNombreIgnoreCase(nombre)) {
                throw new IllegalArgumentException("Ya existe un foco con ese nombre");
            }
            if (focoRepository.existsByCodigoIgnoreCase(codigo)) {
                throw new IllegalArgumentException("Ya existe un foco con ese codigo");
            }

            FocoEntity entity = new FocoEntity();
            entity.setNombre(nombre);
            entity.setCodigo(codigo);
            focoRepository.save(entity);

            return "redirect:/dashboard/focos?success=Foco creado correctamente";
        } catch (IllegalArgumentException ex) {
            return "redirect:/dashboard/focos?error=" + ex.getMessage();
        }
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id, @Valid UpdateFocoRequest request) {
        try {
            FocoEntity entity = focoRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Foco no encontrado"));

            String nombre = normalize(request.getNombre(), "El nombre es obligatorio");
            String codigo = normalize(request.getCodigo(), "El codigo es obligatorio").toUpperCase();

            if (focoRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
                throw new IllegalArgumentException("Ya existe un foco con ese nombre");
            }
            if (focoRepository.existsByCodigoIgnoreCaseAndIdNot(codigo, id)) {
                throw new IllegalArgumentException("Ya existe un foco con ese codigo");
            }

            entity.setNombre(nombre);
            entity.setCodigo(codigo);
            focoRepository.save(entity);

            return "redirect:/dashboard/focos?success=Foco actualizado correctamente";
        } catch (IllegalArgumentException ex) {
            return "redirect:/dashboard/focos?error=" + ex.getMessage();
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        try {
            if (!focoRepository.existsById(id)) {
                throw new IllegalArgumentException("Foco no encontrado");
            }
            focoRepository.deleteById(id);
            return "redirect:/dashboard/focos?success=Foco eliminado correctamente";
        } catch (DataIntegrityViolationException ex) {
            return "redirect:/dashboard/focos?error=No se puede eliminar el foco porque esta en uso";
        } catch (IllegalArgumentException ex) {
            return "redirect:/dashboard/focos?error=" + ex.getMessage();
        }
    }

    private String normalize(String value, String message) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return normalized;
    }
}
