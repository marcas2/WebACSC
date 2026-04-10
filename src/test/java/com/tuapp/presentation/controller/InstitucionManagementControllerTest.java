package com.tuapp.presentation.controller;

import com.tuapp.infrastructure.persistence.entity.InstitucionEntity;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstitucionManagementControllerTest {

    @Mock
    private InstitucionJpaRepository institucionRepository;

    private InstitucionManagementController controller;

    @BeforeEach
    void setUp() {
        controller = new InstitucionManagementController(institucionRepository);
    }

    @Test
    void hu015_shouldListInstitutionsWhenAccessingModule() {
        InstitucionEntity institucion = buildInstitucion(1L, "Hospital Norte", true);
        when(institucionRepository.findAllByOrderByNombreAsc()).thenReturn(List.of(institucion));
        Model model = new ExtendedModelMap();

        String view = controller.list(model, null, null);

        assertEquals("institution-management", view);
        assertEquals(1, ((List<?>) model.getAttribute("instituciones")).size());
    }

    @Test
    void hu015_shouldShowInstitutionsWithRelevantInfo() {
        InstitucionEntity activa = buildInstitucion(1L, "Hospital Central", true);
        InstitucionEntity inactiva = buildInstitucion(2L, "Hospital Sur", false);
        when(institucionRepository.findAllByOrderByNombreAsc()).thenReturn(List.of(activa, inactiva));
        Model model = new ExtendedModelMap();

        controller.list(model, null, null);

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

        String view = controller.list(model, null, null);

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

        String redirect = controller.delete(1L);

        assertEquals("redirect:/dashboard/instituciones?success=Institucion eliminada correctamente", redirect);
    }

    @Test
    void hu018_shouldPreventDeleteWhenInstitutionHasDependencies() {
        when(institucionRepository.existsById(1L)).thenReturn(true);
        doThrow(new IllegalArgumentException("La institucion tiene consultorios asociados"))
                .when(institucionRepository).deleteById(1L);

        String redirect = controller.delete(1L);

        assertEquals("redirect:/dashboard/instituciones?error=La institucion tiene consultorios asociados", redirect);
    }

    @Test
    void hu018_shouldShowErrorWhenDeleteFails() {
        when(institucionRepository.existsById(404L)).thenReturn(false);

        String redirect = controller.delete(404L);

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
