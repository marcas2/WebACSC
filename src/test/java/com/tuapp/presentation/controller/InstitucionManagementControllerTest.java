package com.tuapp.presentation.controller;

import com.tuapp.infrastructure.persistence.entity.InstitucionEntity;
import com.tuapp.infrastructure.persistence.repository.ConsultorioJpaRepository;
import com.tuapp.infrastructure.persistence.repository.DiagnosticJpaRepository;
import com.tuapp.infrastructure.persistence.repository.InstitucionJpaRepository;
import com.tuapp.presentation.dto.CreateInstitucionRequest;
import com.tuapp.presentation.dto.UpdateInstitucionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstitucionManagementControllerTest {

    @Mock
    private InstitucionJpaRepository institucionRepository;

    @Mock
    private ConsultorioJpaRepository consultorioRepository;

    @Mock
    private DiagnosticJpaRepository diagnosticRepository;

    private InstitucionManagementController controller;

    @BeforeEach
    void setUp() {
        controller = new InstitucionManagementController(institucionRepository, consultorioRepository, diagnosticRepository);
    }

    @Test
    void hu015_shouldListInstitutionsWhenAccessingModule() {
        InstitucionEntity institucion = buildInstitucion(1L, "Hospital Norte", true);
        when(institucionRepository.findAllByOrderByNombreAsc()).thenReturn(List.of(institucion));
        Model model = new ExtendedModelMap();

        String view = controller.list(model, null, null, null, false, false, 0);

        assertEquals("institution-management", view);
        assertEquals(1, ((List<?>) model.getAttribute("instituciones")).size());
    }

    @Test
    void hu015_shouldShowInstitutionsWithRelevantInfo() {
        InstitucionEntity activa = buildInstitucion(1L, "Hospital Central", true);
        InstitucionEntity inactiva = buildInstitucion(2L, "Hospital Sur", false);
        when(institucionRepository.findAllByOrderByNombreAsc()).thenReturn(List.of(activa, inactiva));
        Model model = new ExtendedModelMap();

        controller.list(model, null, null, null, false, false, 0);

        @SuppressWarnings("unchecked")
        List<InstitucionEntity> instituciones = (List<InstitucionEntity>) model.getAttribute("instituciones");
        assertEquals("Hospital Central", instituciones.get(0).getNombre());
        assertEquals(true, instituciones.get(0).getActivo());
        assertEquals("Hospital Sur", instituciones.get(1).getNombre());
        assertEquals(false, instituciones.get(1).getActivo());
    }

    @Test
    void hu015_shouldHandleEmptyInstitutionList() {
        when(institucionRepository.findAllByOrderByNombreAsc()).thenReturn(List.of());
        Model model = new ExtendedModelMap();

        String view = controller.list(model, null, null, null, false, false, 0);

        assertEquals("institution-management", view);
        assertEquals(List.of(), model.getAttribute("instituciones"));
    }

    @Test
    void hu016_shouldCreateInstitutionSuccessfully() {
        CreateInstitucionRequest request = new CreateInstitucionRequest();
        request.setNombre("Hospital Central");
        when(institucionRepository.existsByNombreIgnoreCase("Hospital Central")).thenReturn(false);

        String redirect = controller.create(request);

        assertEquals("redirect:/dashboard/instituciones?success=Institucion creada correctamente", redirect);
    }

    @Test
    void hu016_shouldValidateRequiredFieldsOnCreate() {
        CreateInstitucionRequest request = new CreateInstitucionRequest();
        request.setNombre("   ");

        String redirect = controller.create(request);

        assertEquals("redirect:/dashboard/instituciones?error=El nombre es obligatorio", redirect);
    }

    @Test
    void hu016_shouldShowErrorWhenCreateFails() {
        CreateInstitucionRequest request = new CreateInstitucionRequest();
        request.setNombre("Hospital Central");
        when(institucionRepository.existsByNombreIgnoreCase("Hospital Central")).thenReturn(true);

        String redirect = controller.create(request);

        assertEquals("redirect:/dashboard/instituciones?error=Ya existe una institucion con ese nombre", redirect);
    }

    @Test
    void hu017_shouldUpdateInstitutionSuccessfully() {
        UpdateInstitucionRequest request = new UpdateInstitucionRequest();
        request.setNombre("Hospital Norte Actualizado");
        request.setActivo(false);
        InstitucionEntity entity = buildInstitucion(1L, "Hospital Norte", true);

        when(institucionRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(institucionRepository.existsByNombreIgnoreCaseAndIdNot("Hospital Norte Actualizado", 1L)).thenReturn(false);

        String redirect = controller.edit(1L, request);

        assertEquals("redirect:/dashboard/instituciones?success=Institucion actualizada correctamente", redirect);
    }

    @Test
    void hu017_shouldShowValidationMessageForInvalidData() {
        UpdateInstitucionRequest request = new UpdateInstitucionRequest();
        request.setNombre("   ");
        request.setActivo(true);
        when(institucionRepository.findById(1L)).thenReturn(Optional.of(buildInstitucion(1L, "Hospital", true)));

        String redirect = controller.edit(1L, request);

        assertEquals("redirect:/dashboard/instituciones?error=El nombre es obligatorio", redirect);
    }

    @Test
    void hu017_shouldNotifyErrorWhenUpdateFails() {
        UpdateInstitucionRequest request = new UpdateInstitucionRequest();
        request.setNombre("Hospital X");
        request.setActivo(true);
        when(institucionRepository.findById(99L)).thenReturn(Optional.empty());

        String redirect = controller.edit(99L, request);

        assertEquals("redirect:/dashboard/instituciones?error=Institucion no encontrada", redirect);
    }

    @Test
    void hu018_shouldDeleteInstitutionWhenConfirmed() {
        when(institucionRepository.existsById(1L)).thenReturn(true);
        when(consultorioRepository.existsByInstitucionId(1L)).thenReturn(false);
        when(diagnosticRepository.countByInstitucion_Id(1L)).thenReturn(0L);

        String redirect = controller.delete(1L, false, false, false);

        assertEquals("redirect:/dashboard/instituciones?success=Institucion eliminada correctamente", redirect);
    }

    @Test
    void hu018_shouldPreventDeleteWhenInstitutionHasDependencies() {
        when(institucionRepository.existsById(1L)).thenReturn(true);
        when(consultorioRepository.existsByInstitucionId(1L)).thenReturn(true);
        when(diagnosticRepository.countByInstitucion_Id(1L)).thenReturn(0L);

        String redirect = controller.delete(1L, false, false, false);

        assertEquals("redirect:/dashboard/instituciones?error=La institucion tiene registros asociados&pendingDeleteInstitutionId=1&pendingDeleteHasConsultorios=true&pendingDeleteHasDiagnostics=false&pendingDiagnosticsCount=0", redirect);
        verify(institucionRepository, never()).deleteById(1L);
    }

    @Test
    void hu018_shouldDeleteInstitutionAndConsultoriosWhenExplicitlyRequested() {
        when(institucionRepository.existsById(1L)).thenReturn(true);
        when(consultorioRepository.existsByInstitucionId(1L)).thenReturn(true);
        when(diagnosticRepository.countByInstitucion_Id(1L)).thenReturn(0L);

        String redirect = controller.delete(1L, true, false, false);

        assertEquals("redirect:/dashboard/instituciones?success=Institucion y consultorios eliminados correctamente", redirect);
        verify(consultorioRepository).deleteByInstitucionId(1L);
        verify(institucionRepository).deleteById(1L);
    }

    @Test
    void hu018_shouldRequestDecisionWhenInstitutionHasDiagnostics() {
        when(institucionRepository.existsById(1L)).thenReturn(true);
        when(consultorioRepository.existsByInstitucionId(1L)).thenReturn(false);
        when(diagnosticRepository.countByInstitucion_Id(1L)).thenReturn(4L);

        String redirect = controller.delete(1L, false, false, false);

        assertEquals("redirect:/dashboard/instituciones?error=La institucion tiene registros asociados&pendingDeleteInstitutionId=1&pendingDeleteHasConsultorios=false&pendingDeleteHasDiagnostics=true&pendingDiagnosticsCount=4", redirect);
        verify(institucionRepository, never()).deleteById(1L);
    }

    @Test
    void hu018_shouldDeleteInstitutionAndDiagnosticsWhenRequested() {
        when(institucionRepository.existsById(1L)).thenReturn(true);
        when(consultorioRepository.existsByInstitucionId(1L)).thenReturn(false);
        when(diagnosticRepository.countByInstitucion_Id(1L)).thenReturn(3L);

        String redirect = controller.delete(1L, false, true, false);

        assertEquals("redirect:/dashboard/instituciones?success=Institucion y diagnosticos eliminados correctamente", redirect);
        verify(diagnosticRepository).deleteDiagnosticDiseaseLinksByInstitucionId(1L);
        verify(diagnosticRepository).deleteByInstitucionIdNative(1L);
        verify(institucionRepository).deleteById(1L);
    }

    @Test
    void hu018_shouldMoveDiagnosticsToNoInformadaWhenRequested() {
        when(institucionRepository.existsById(1L)).thenReturn(true);
        when(institucionRepository.existsById(3L)).thenReturn(true);
        when(consultorioRepository.existsByInstitucionId(1L)).thenReturn(false);
        when(diagnosticRepository.countByInstitucion_Id(1L)).thenReturn(2L);

        String redirect = controller.delete(1L, false, false, true);

        assertEquals("redirect:/dashboard/instituciones?success=Institucion eliminada y diagnosticos reasignados a NO INFORMADA", redirect);
        verify(diagnosticRepository).reassignInstitucion(1L, 3L);
        verify(institucionRepository).deleteById(1L);
    }

    @Test
    void hu018_shouldFailWhenNoInformadaInstitutionDoesNotExist() {
        when(institucionRepository.existsById(1L)).thenReturn(true);
        when(institucionRepository.existsById(3L)).thenReturn(false);
        when(consultorioRepository.existsByInstitucionId(1L)).thenReturn(false);
        when(diagnosticRepository.countByInstitucion_Id(1L)).thenReturn(2L);

        String redirect = controller.delete(1L, false, false, true);

        assertEquals("redirect:/dashboard/instituciones?error=No existe la institucion NO INFORMADA (ID 3)", redirect);
    }

    @Test
    void hu018_shouldPreventDeletingNoInformadaInstitution() {
        when(institucionRepository.existsById(3L)).thenReturn(true);

        String redirect = controller.delete(3L, false, false, false);

        assertEquals("redirect:/dashboard/instituciones?error=No se puede eliminar la institucion NO INFORMADA", redirect);
        verify(institucionRepository, never()).deleteById(3L);
    }

    @Test
    void hu018_shouldShowErrorWhenDeleteFails() {
        when(institucionRepository.existsById(404L)).thenReturn(false);

        String redirect = controller.delete(404L, false, false, false);

        assertEquals("redirect:/dashboard/instituciones?error=Institucion no encontrada", redirect);
    }

    private InstitucionEntity buildInstitucion(Long id, String nombre, Boolean activo) {
        InstitucionEntity entity = new InstitucionEntity();
        entity.setId(id);
        entity.setNombre(nombre);
        entity.setActivo(activo);
        return entity;
    }
}
