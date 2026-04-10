package com.tuapp.presentation.controller;

import com.tuapp.infrastructure.persistence.entity.EnfermedadBaseEntity;
import com.tuapp.infrastructure.persistence.repository.EnfermedadBaseJpaRepository;
import com.tuapp.presentation.dto.CreateEnfermedadBaseRequest;
import com.tuapp.presentation.dto.UpdateEnfermedadBaseRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnfermedadBaseManagementControllerTest {

    @Mock
    private EnfermedadBaseJpaRepository enfermedadRepository;

    private EnfermedadBaseManagementController controller;

    @BeforeEach
    void setUp() {
        controller = new EnfermedadBaseManagementController(enfermedadRepository);
    }

    @Test
    void hu027_shouldListDiseasesWhenAccessingModule() {
        EnfermedadBaseEntity enfermedad = buildEnfermedad(1L, "Hipertension");
        when(enfermedadRepository.findAllByOrderByNombreAsc()).thenReturn(List.of(enfermedad));
        Model model = new ExtendedModelMap();

        String view = controller.list(model, null, null);

        assertEquals("enfermedad-management", view);
        assertEquals(1, ((List<?>) model.getAttribute("enfermedades")).size());
    }

    @Test
    void hu027_shouldShowDiseasesWithRelevantInfo() {
        EnfermedadBaseEntity a = buildEnfermedad(1L, "Hipertension");
        EnfermedadBaseEntity b = buildEnfermedad(2L, "Diabetes");
        when(enfermedadRepository.findAllByOrderByNombreAsc()).thenReturn(List.of(a, b));
        Model model = new ExtendedModelMap();

        controller.list(model, null, null);

        @SuppressWarnings("unchecked")
        List<EnfermedadBaseEntity> enfermedades = (List<EnfermedadBaseEntity>) model.getAttribute("enfermedades");
        assertEquals("Hipertension", enfermedades.get(0).getNombre());
        assertEquals("Diabetes", enfermedades.get(1).getNombre());
    }

    @Test
    void hu027_shouldHandleEmptyDiseaseList() {
        when(enfermedadRepository.findAllByOrderByNombreAsc()).thenReturn(List.of());
        Model model = new ExtendedModelMap();

        String view = controller.list(model, null, null);

        assertEquals("enfermedad-management", view);
        assertEquals(List.of(), model.getAttribute("enfermedades"));
    }

    @Test
    void hu028_shouldCreateDiseaseSuccessfully() {
        CreateEnfermedadBaseRequest request = new CreateEnfermedadBaseRequest();
        request.setNombre("Hipertension");
        when(enfermedadRepository.existsByNombreIgnoreCase("Hipertension")).thenReturn(false);

        String redirect = controller.create(request);

        assertEquals("redirect:/dashboard/enfermedades?success=Enfermedad creada correctamente", redirect);
    }

    @Test
    void hu028_shouldValidateRequiredFieldsOnCreate() {
        CreateEnfermedadBaseRequest request = new CreateEnfermedadBaseRequest();
        request.setNombre("   ");

        String redirect = controller.create(request);

        assertEquals("redirect:/dashboard/enfermedades?error=El nombre es obligatorio", redirect);
    }

    @Test
    void hu028_shouldNotifyErrorWhenCreateFails() {
        CreateEnfermedadBaseRequest request = new CreateEnfermedadBaseRequest();
        request.setNombre("Hipertension");
        when(enfermedadRepository.existsByNombreIgnoreCase("Hipertension")).thenReturn(true);

        String redirect = controller.create(request);

        assertEquals("redirect:/dashboard/enfermedades?error=Ya existe una enfermedad con ese nombre", redirect);
    }

    @Test
    void hu029_shouldUpdateDiseaseSuccessfully() {
        UpdateEnfermedadBaseRequest request = new UpdateEnfermedadBaseRequest();
        request.setNombre("Diabetes");
        EnfermedadBaseEntity entity = buildEnfermedad(1L, "Hipertension");

        when(enfermedadRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(enfermedadRepository.existsByNombreIgnoreCaseAndIdNot("Diabetes", 1L)).thenReturn(false);

        String redirect = controller.edit(1L, request);

        assertEquals("redirect:/dashboard/enfermedades?success=Enfermedad actualizada correctamente", redirect);
    }

    @Test
    void hu029_shouldShowValidationMessageForInvalidData() {
        UpdateEnfermedadBaseRequest request = new UpdateEnfermedadBaseRequest();
        request.setNombre(" ");
        when(enfermedadRepository.findById(1L)).thenReturn(Optional.of(buildEnfermedad(1L, "Hipertension")));

        String redirect = controller.edit(1L, request);

        assertEquals("redirect:/dashboard/enfermedades?error=El nombre es obligatorio", redirect);
    }

    @Test
    void hu029_shouldNotifyErrorWhenUpdateFails() {
        UpdateEnfermedadBaseRequest request = new UpdateEnfermedadBaseRequest();
        request.setNombre("Diabetes");
        when(enfermedadRepository.findById(99L)).thenReturn(Optional.empty());

        String redirect = controller.edit(99L, request);

        assertEquals("redirect:/dashboard/enfermedades?error=Enfermedad no encontrada", redirect);
    }

    @Test
    void hu030_shouldDeleteDiseaseWhenConfirmed() {
        when(enfermedadRepository.existsById(1L)).thenReturn(true);

        String redirect = controller.delete(1L);

        assertEquals("redirect:/dashboard/enfermedades?success=Enfermedad eliminada correctamente", redirect);
    }

    @Test
    void hu030_shouldPreventDeleteWhenDiseaseHasDependencies() {
        when(enfermedadRepository.existsById(1L)).thenReturn(true);
        doThrow(new DataIntegrityViolationException("FK violation")).when(enfermedadRepository).deleteById(1L);

        String redirect = controller.delete(1L);

        assertEquals("redirect:/dashboard/enfermedades?error=No se puede eliminar la enfermedad porque esta en uso", redirect);
    }

    @Test
    void hu030_shouldShowErrorWhenDeleteFails() {
        when(enfermedadRepository.existsById(404L)).thenReturn(false);

        String redirect = controller.delete(404L);

        assertEquals("redirect:/dashboard/enfermedades?error=Enfermedad no encontrada", redirect);
    }

    private EnfermedadBaseEntity buildEnfermedad(Long id, String nombre) {
        EnfermedadBaseEntity entity = new EnfermedadBaseEntity();
        entity.setId(id);
        entity.setNombre(nombre);
        return entity;
    }
}
