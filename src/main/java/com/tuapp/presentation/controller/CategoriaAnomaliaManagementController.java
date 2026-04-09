package com.tuapp.presentation.controller;

import com.tuapp.infrastructure.persistence.entity.CategoriaAnomaliaEntity;
import com.tuapp.infrastructure.persistence.repository.CategoriaAnomaliaJpaRepository;
import com.tuapp.presentation.dto.CreateCategoriaAnomaliaRequest;
import com.tuapp.presentation.dto.UpdateCategoriaAnomaliaRequest;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/dashboard/categorias-anomalias")
public class CategoriaAnomaliaManagementController {

    private final CategoriaAnomaliaJpaRepository categoriaRepository;

    public CategoriaAnomaliaManagementController(CategoriaAnomaliaJpaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @GetMapping
    public String list(Model model,
                       @RequestParam(required = false) String success,
                       @RequestParam(required = false) String error) {
        model.addAttribute("categorias", categoriaRepository.findAllByOrderByNombreAsc());
        model.addAttribute("success", success);
        model.addAttribute("error", error);
        return "categoria-anomalia-management";
    }

    @PostMapping("/create")
    public String create(@Valid CreateCategoriaAnomaliaRequest request) {
        try {
            String nombre = normalize(request.getNombre(), "El nombre es obligatorio");
            String codigo = normalize(request.getCodigo(), "El codigo es obligatorio").toUpperCase();

            if (categoriaRepository.existsByNombreIgnoreCase(nombre)) {
                throw new IllegalArgumentException("Ya existe una categoria con ese nombre");
            }
            if (categoriaRepository.existsByCodigoIgnoreCase(codigo)) {
                throw new IllegalArgumentException("Ya existe una categoria con ese codigo");
            }

            CategoriaAnomaliaEntity entity = new CategoriaAnomaliaEntity();
            entity.setNombre(nombre);
            entity.setCodigo(codigo);
            categoriaRepository.save(entity);

            return "redirect:/dashboard/categorias-anomalias?success=Categoria creada correctamente";
        } catch (IllegalArgumentException ex) {
            return "redirect:/dashboard/categorias-anomalias?error=" + ex.getMessage();
        }
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id, @Valid UpdateCategoriaAnomaliaRequest request) {
        try {
            CategoriaAnomaliaEntity entity = categoriaRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Categoria no encontrada"));

            String nombre = normalize(request.getNombre(), "El nombre es obligatorio");
            String codigo = normalize(request.getCodigo(), "El codigo es obligatorio").toUpperCase();

            if (categoriaRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
                throw new IllegalArgumentException("Ya existe una categoria con ese nombre");
            }
            if (categoriaRepository.existsByCodigoIgnoreCaseAndIdNot(codigo, id)) {
                throw new IllegalArgumentException("Ya existe una categoria con ese codigo");
            }

            entity.setNombre(nombre);
            entity.setCodigo(codigo);
            categoriaRepository.save(entity);

            return "redirect:/dashboard/categorias-anomalias?success=Categoria actualizada correctamente";
        } catch (IllegalArgumentException ex) {
            return "redirect:/dashboard/categorias-anomalias?error=" + ex.getMessage();
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        try {
            if (!categoriaRepository.existsById(id)) {
                throw new IllegalArgumentException("Categoria no encontrada");
            }
            categoriaRepository.deleteById(id);
            return "redirect:/dashboard/categorias-anomalias?success=Categoria eliminada correctamente";
        } catch (DataIntegrityViolationException ex) {
            return "redirect:/dashboard/categorias-anomalias?error=No se puede eliminar la categoria porque esta en uso";
        } catch (IllegalArgumentException ex) {
            return "redirect:/dashboard/categorias-anomalias?error=" + ex.getMessage();
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
