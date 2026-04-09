package com.tuapp.presentation.controller;

import com.tuapp.infrastructure.persistence.entity.ConsultorioEntity;
import com.tuapp.infrastructure.persistence.entity.InstitucionEntity;
import com.tuapp.infrastructure.persistence.repository.ConsultorioJpaRepository;
import com.tuapp.infrastructure.persistence.repository.InstitucionJpaRepository;
import com.tuapp.presentation.dto.CreateConsultorioRequest;
import com.tuapp.presentation.dto.UpdateConsultorioRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/dashboard/instituciones/{institucionId}/consultorios")
public class ConsultorioManagementController {

    private final ConsultorioJpaRepository consultorioRepository;
    private final InstitucionJpaRepository institucionRepository;

    public ConsultorioManagementController(ConsultorioJpaRepository consultorioRepository,
                                           InstitucionJpaRepository institucionRepository) {
        this.consultorioRepository = consultorioRepository;
        this.institucionRepository = institucionRepository;
    }

    @GetMapping
    public String list(@PathVariable Long institucionId,
                       Model model,
                       @RequestParam(required = false) String success,
                       @RequestParam(required = false) String error) {

        InstitucionEntity institucion = institucionRepository.findById(institucionId)
                .orElseThrow(() -> new IllegalArgumentException("Institucion no encontrada"));

        model.addAttribute("institucion", institucion);
        model.addAttribute("consultorios", consultorioRepository.findByInstitucionIdOrderByNombreAsc(institucionId));
        model.addAttribute("success", success);
        model.addAttribute("error", error);
        return "consultorio-management";
    }

    @PostMapping("/create")
    public String create(@PathVariable Long institucionId,
                         @Valid CreateConsultorioRequest request) {
        try {
            InstitucionEntity institucion = institucionRepository.findById(institucionId)
                    .orElseThrow(() -> new IllegalArgumentException("Institucion no encontrada"));

            String nombre = normalizeText(request.getNombre(), "El nombre es obligatorio");
            String codigo = normalizeText(request.getCodigo(), "El codigo es obligatorio").toUpperCase();

            if (consultorioRepository.existsByCodigoIgnoreCase(codigo)) {
                throw new IllegalArgumentException("Ya existe un consultorio con ese codigo");
            }

            if (consultorioRepository.existsByNombreIgnoreCaseAndInstitucionId(nombre, institucionId)) {
                throw new IllegalArgumentException("Ya existe un consultorio con ese nombre en la institucion");
            }

            ConsultorioEntity entity = new ConsultorioEntity();
            entity.setNombre(nombre);
            entity.setCodigo(codigo);
            entity.setActivo(true);
            entity.setInstitucion(institucion);
            consultorioRepository.save(entity);

            return "redirect:/dashboard/instituciones/" + institucionId + "/consultorios?success=Consultorio creado correctamente";
        } catch (IllegalArgumentException ex) {
            return "redirect:/dashboard/instituciones/" + institucionId + "/consultorios?error=" + ex.getMessage();
        }
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long institucionId,
                       @PathVariable Long id,
                       @Valid UpdateConsultorioRequest request) {
        try {
            ConsultorioEntity entity = consultorioRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Consultorio no encontrado"));

            if (!entity.getInstitucion().getId().equals(institucionId)) {
                throw new IllegalArgumentException("El consultorio no pertenece a la institucion seleccionada");
            }

            String nombre = normalizeText(request.getNombre(), "El nombre es obligatorio");
            String codigo = normalizeText(request.getCodigo(), "El codigo es obligatorio").toUpperCase();

            if (consultorioRepository.existsByCodigoIgnoreCaseAndIdNot(codigo, id)) {
                throw new IllegalArgumentException("Ya existe un consultorio con ese codigo");
            }

            if (consultorioRepository.existsByNombreIgnoreCaseAndInstitucionIdAndIdNot(nombre, institucionId, id)) {
                throw new IllegalArgumentException("Ya existe un consultorio con ese nombre en la institucion");
            }

            entity.setNombre(nombre);
            entity.setCodigo(codigo);
            entity.setActivo(request.getActivo());
            consultorioRepository.save(entity);

            return "redirect:/dashboard/instituciones/" + institucionId + "/consultorios?success=Consultorio actualizado correctamente";
        } catch (IllegalArgumentException ex) {
            return "redirect:/dashboard/instituciones/" + institucionId + "/consultorios?error=" + ex.getMessage();
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long institucionId,
                         @PathVariable Long id) {
        try {
            ConsultorioEntity entity = consultorioRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Consultorio no encontrado"));

            if (!entity.getInstitucion().getId().equals(institucionId)) {
                throw new IllegalArgumentException("El consultorio no pertenece a la institucion seleccionada");
            }

            consultorioRepository.delete(entity);
            return "redirect:/dashboard/instituciones/" + institucionId + "/consultorios?success=Consultorio eliminado correctamente";
        } catch (IllegalArgumentException ex) {
            return "redirect:/dashboard/instituciones/" + institucionId + "/consultorios?error=" + ex.getMessage();
        }
    }

    private String normalizeText(String value, String message) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return normalized;
    }
}
