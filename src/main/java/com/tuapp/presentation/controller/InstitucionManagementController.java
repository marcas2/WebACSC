package com.tuapp.presentation.controller;

import com.tuapp.infrastructure.persistence.entity.InstitucionEntity;
import com.tuapp.infrastructure.persistence.repository.ConsultorioJpaRepository;
import com.tuapp.infrastructure.persistence.repository.DiagnosticJpaRepository;
import com.tuapp.infrastructure.persistence.repository.InstitucionJpaRepository;
import com.tuapp.presentation.dto.CreateInstitucionRequest;
import com.tuapp.presentation.dto.UpdateInstitucionRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/dashboard/instituciones")
public class InstitucionManagementController {

    private static final long INSTITUCION_NO_INFORMADA_ID = 3L;

    private final InstitucionJpaRepository institucionRepository;
    private final ConsultorioJpaRepository consultorioRepository;
    private final DiagnosticJpaRepository diagnosticRepository;

    public InstitucionManagementController(InstitucionJpaRepository institucionRepository,
                                           ConsultorioJpaRepository consultorioRepository,
                                           DiagnosticJpaRepository diagnosticRepository) {
        this.institucionRepository = institucionRepository;
        this.consultorioRepository = consultorioRepository;
        this.diagnosticRepository = diagnosticRepository;
    }

    @GetMapping
    public String list(Model model,
                       @RequestParam(required = false) String success,
                       @RequestParam(required = false) String error,
                       @RequestParam(required = false) Long pendingDeleteInstitutionId,
                       @RequestParam(defaultValue = "false") boolean pendingDeleteHasConsultorios,
                       @RequestParam(defaultValue = "false") boolean pendingDeleteHasDiagnostics,
                       @RequestParam(defaultValue = "0") long pendingDiagnosticsCount) {
        model.addAttribute("instituciones", institucionRepository.findAllByOrderByNombreAsc());
        model.addAttribute("success", success);
        model.addAttribute("error", error);
        model.addAttribute("pendingDeleteInstitutionId", pendingDeleteInstitutionId);
        model.addAttribute("pendingDeleteHasConsultorios", pendingDeleteHasConsultorios);
        model.addAttribute("pendingDeleteHasDiagnostics", pendingDeleteHasDiagnostics);
        model.addAttribute("pendingDiagnosticsCount", pendingDiagnosticsCount);
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
    @Transactional
    public String delete(@PathVariable Long id,
                         @RequestParam(defaultValue = "false") boolean deleteConsultorios,
                         @RequestParam(defaultValue = "false") boolean deleteDiagnostics,
                         @RequestParam(defaultValue = "false") boolean moveDiagnosticsToNoInformada) {
        try {
            if (!institucionRepository.existsById(id)) {
                throw new IllegalArgumentException("Institucion no encontrada");
            }

            if (id == INSTITUCION_NO_INFORMADA_ID) {
                throw new IllegalArgumentException("No se puede eliminar la institucion NO INFORMADA");
            }

            boolean hasConsultorios = consultorioRepository.existsByInstitucionId(id);
            long diagnosticsCount = diagnosticRepository.countByInstitucion_Id(id);
            boolean hasDiagnostics = diagnosticsCount > 0;

            boolean requiresConsultorioDecision = hasConsultorios && !deleteConsultorios;
            boolean requiresDiagnosticDecision = hasDiagnostics && !deleteDiagnostics && !moveDiagnosticsToNoInformada;
            if (requiresConsultorioDecision || requiresDiagnosticDecision) {
                return "redirect:/dashboard/instituciones?error=La institucion tiene registros asociados"
                        + "&pendingDeleteInstitutionId=" + id
                        + "&pendingDeleteHasConsultorios=" + hasConsultorios
                        + "&pendingDeleteHasDiagnostics=" + hasDiagnostics
                        + "&pendingDiagnosticsCount=" + diagnosticsCount;
            }

            if (deleteConsultorios) {
                consultorioRepository.deleteByInstitucionId(id);
            }

            if (deleteDiagnostics && hasDiagnostics) {
                diagnosticRepository.deleteDiagnosticDiseaseLinksByInstitucionId(id);
                diagnosticRepository.deleteByInstitucionIdNative(id);
            }

            if (moveDiagnosticsToNoInformada && hasDiagnostics) {
                if (!institucionRepository.existsById(INSTITUCION_NO_INFORMADA_ID)) {
                    throw new IllegalArgumentException("No existe la institucion NO INFORMADA (ID 3)");
                }
                diagnosticRepository.reassignInstitucion(id, INSTITUCION_NO_INFORMADA_ID);
            }

            institucionRepository.deleteById(id);

            if (deleteConsultorios && deleteDiagnostics) {
                return "redirect:/dashboard/instituciones?success=Institucion, consultorios y diagnosticos eliminados correctamente";
            }
            if (deleteConsultorios && moveDiagnosticsToNoInformada) {
                return "redirect:/dashboard/instituciones?success=Institucion y consultorios eliminados, diagnosticos reasignados a NO INFORMADA";
            }
            if (deleteDiagnostics) {
                return "redirect:/dashboard/instituciones?success=Institucion y diagnosticos eliminados correctamente";
            }
            if (moveDiagnosticsToNoInformada) {
                return "redirect:/dashboard/instituciones?success=Institucion eliminada y diagnosticos reasignados a NO INFORMADA";
            }
            if (deleteConsultorios) {
                return "redirect:/dashboard/instituciones?success=Institucion y consultorios eliminados correctamente";
            }

            return "redirect:/dashboard/instituciones?success=Institucion eliminada correctamente";
        } catch (IllegalArgumentException ex) {
            return "redirect:/dashboard/instituciones?error=" + ex.getMessage();
        } catch (RuntimeException ex) {
            return "redirect:/dashboard/instituciones?error=No se pudo eliminar la institucion. Revisa dependencias activas";
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
