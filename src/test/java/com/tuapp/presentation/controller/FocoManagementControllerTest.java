package com.tuapp.presentation.controller;

import com.tuapp.infrastructure.persistence.entity.FocoEntity;
import com.tuapp.infrastructure.persistence.repository.FocoJpaRepository;
import com.tuapp.presentation.dto.CreateFocoRequest;
import com.tuapp.presentation.dto.UpdateFocoRequest;
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
class FocoManagementControllerTest {

    @Mock
    private FocoJpaRepository focoRepository;

    private FocoManagementController controller;

    @BeforeEach
    void setUp() {
        controller = new FocoManagementController(focoRepository);
    }

    @Test
    void hu031_shouldListFocosWhenAccessingModule() {
        FocoEntity foco = buildFoco(1L, "Aortico", "AOR");
        when(focoRepository.findAllByOrderByNombreAsc()).thenReturn(List.of(foco));
        Model model = new ExtendedModelMap();

        String view = controller.list(model, null, null);

        assertEquals("foco-management", view);
        assertEquals(1, ((List<?>) model.getAttribute("focos")).size());
    }

    @Test
    void hu031_shouldShowFocosWithRelevantInfo() {
        FocoEntity a = buildFoco(1L, "Aortico", "AOR");
        FocoEntity b = buildFoco(2L, "Mitral", "MIT");
        when(focoRepository.findAllByOrderByNombreAsc()).thenReturn(List.of(a, b));
        Model model = new ExtendedModelMap();

        controller.list(model, null, null);

        @SuppressWarnings("unchecked")
        List<FocoEntity> focos = (List<FocoEntity>) model.getAttribute("focos");
        assertEquals("Aortico", focos.get(0).getNombre());
        assertEquals("AOR", focos.get(0).getCodigo());
        assertEquals("Mitral", focos.get(1).getNombre());
    }

    @Test
    void hu031_shouldHandleEmptyFocoList() {
        when(focoRepository.findAllByOrderByNombreAsc()).thenReturn(List.of());
        Model model = new ExtendedModelMap();

        String view = controller.list(model, null, null);

        assertEquals("foco-management", view);
        assertEquals(List.of(), model.getAttribute("focos"));
    }

    @Test
    void hu032_shouldCreateFocoSuccessfully() {
        CreateFocoRequest request = new CreateFocoRequest();
        request.setNombre("Aortico");
        request.setCodigo("aor");
        when(focoRepository.existsByNombreIgnoreCase("Aortico")).thenReturn(false);
        when(focoRepository.existsByCodigoIgnoreCase("AOR")).thenReturn(false);

        String redirect = controller.create(request);

        assertEquals("redirect:/dashboard/focos?success=Foco creado correctamente", redirect);
    }

    @Test
    void hu032_shouldValidateRequiredFieldsOnCreate() {
        CreateFocoRequest request = new CreateFocoRequest();
        request.setNombre("   ");
        request.setCodigo("AOR");

        String redirect = controller.create(request);

        assertEquals("redirect:/dashboard/focos?error=El nombre es obligatorio", redirect);
    }

    @Test
    void hu032_shouldNotifyErrorWhenCreateFails() {
        CreateFocoRequest request = new CreateFocoRequest();
        request.setNombre("Aortico");
        request.setCodigo("AOR");
        when(focoRepository.existsByNombreIgnoreCase("Aortico")).thenReturn(true);

        String redirect = controller.create(request);

        assertEquals("redirect:/dashboard/focos?error=Ya existe un foco con ese nombre", redirect);
    }

    @Test
    void hu033_shouldUpdateFocoSuccessfully() {
        UpdateFocoRequest request = new UpdateFocoRequest();
        request.setNombre("Mitral");
        request.setCodigo("MIT");
        FocoEntity entity = buildFoco(1L, "Aortico", "AOR");

        when(focoRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(focoRepository.existsByNombreIgnoreCaseAndIdNot("Mitral", 1L)).thenReturn(false);
        when(focoRepository.existsByCodigoIgnoreCaseAndIdNot("MIT", 1L)).thenReturn(false);

        String redirect = controller.edit(1L, request);

        assertEquals("redirect:/dashboard/focos?success=Foco actualizado correctamente", redirect);
    }

    @Test
    void hu033_shouldShowValidationMessageForInvalidData() {
        UpdateFocoRequest request = new UpdateFocoRequest();
        request.setNombre(" ");
        request.setCodigo("MIT");
        when(focoRepository.findById(1L)).thenReturn(Optional.of(buildFoco(1L, "Aortico", "AOR")));

        String redirect = controller.edit(1L, request);

        assertEquals("redirect:/dashboard/focos?error=El nombre es obligatorio", redirect);
    }

    @Test
    void hu033_shouldNotifyErrorWhenUpdateFails() {
        UpdateFocoRequest request = new UpdateFocoRequest();
        request.setNombre("Mitral");
        request.setCodigo("MIT");
        when(focoRepository.findById(99L)).thenReturn(Optional.empty());

        String redirect = controller.edit(99L, request);

        assertEquals("redirect:/dashboard/focos?error=Foco no encontrado", redirect);
    }

    @Test
    void hu034_shouldDeleteFocoWhenConfirmed() {
        when(focoRepository.existsById(1L)).thenReturn(true);

        String redirect = controller.delete(1L);

        assertEquals("redirect:/dashboard/focos?success=Foco eliminado correctamente", redirect);
    }

    @Test
    void hu034_shouldPreventDeleteWhenFocoHasDependencies() {
        when(focoRepository.existsById(1L)).thenReturn(true);
        doThrow(new DataIntegrityViolationException("FK violation")).when(focoRepository).deleteById(1L);

        String redirect = controller.delete(1L);

        assertEquals("redirect:/dashboard/focos?error=No se puede eliminar el foco porque esta en uso", redirect);
    }

    @Test
    void hu034_shouldShowErrorWhenDeleteFails() {
        when(focoRepository.existsById(404L)).thenReturn(false);

        String redirect = controller.delete(404L);

        assertEquals("redirect:/dashboard/focos?error=Foco no encontrado", redirect);
    }

    private FocoEntity buildFoco(Long id, String nombre, String codigo) {
        FocoEntity entity = new FocoEntity();
        entity.setId(id);
        entity.setNombre(nombre);
        entity.setCodigo(codigo);
        return entity;
    }
}
