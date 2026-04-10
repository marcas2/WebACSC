package com.tuapp.presentation.controller;

import com.tuapp.infrastructure.persistence.entity.CategoriaAnomaliaEntity;
import com.tuapp.infrastructure.persistence.repository.CategoriaAnomaliaJpaRepository;
import com.tuapp.presentation.dto.CreateCategoriaAnomaliaRequest;
import com.tuapp.presentation.dto.UpdateCategoriaAnomaliaRequest;
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
class CategoriaAnomaliaManagementControllerTest {

    @Mock
    private CategoriaAnomaliaJpaRepository categoriaRepository;

    private CategoriaAnomaliaManagementController controller;

    @BeforeEach
    void setUp() {
        controller = new CategoriaAnomaliaManagementController(categoriaRepository);
    }

    @Test
    void hu023_shouldListCategoriesWhenAccessingModule() {
        CategoriaAnomaliaEntity categoria = buildCategoria(1L, "Estenosis", "EST");
        when(categoriaRepository.findAllByOrderByNombreAsc()).thenReturn(List.of(categoria));
        Model model = new ExtendedModelMap();

        String view = controller.list(model, null, null);

        assertEquals("categoria-anomalia-management", view);
        assertEquals(1, ((List<?>) model.getAttribute("categorias")).size());
    }

    @Test
    void hu023_shouldShowCategoriesWithRelevantInfo() {
        CategoriaAnomaliaEntity a = buildCategoria(1L, "Estenosis", "EST");
        CategoriaAnomaliaEntity b = buildCategoria(2L, "Insuficiencia", "INS");
        when(categoriaRepository.findAllByOrderByNombreAsc()).thenReturn(List.of(a, b));
        Model model = new ExtendedModelMap();

        controller.list(model, null, null);

        @SuppressWarnings("unchecked")
        List<CategoriaAnomaliaEntity> categorias = (List<CategoriaAnomaliaEntity>) model.getAttribute("categorias");
        assertEquals("Estenosis", categorias.get(0).getNombre());
        assertEquals("EST", categorias.get(0).getCodigo());
        assertEquals("Insuficiencia", categorias.get(1).getNombre());
    }

    @Test
    void hu023_shouldHandleEmptyCategoryList() {
        when(categoriaRepository.findAllByOrderByNombreAsc()).thenReturn(List.of());
        Model model = new ExtendedModelMap();

        String view = controller.list(model, null, null);

        assertEquals("categoria-anomalia-management", view);
        assertEquals(List.of(), model.getAttribute("categorias"));
    }

    @Test
    void hu024_shouldCreateCategorySuccessfully() {
        CreateCategoriaAnomaliaRequest request = new CreateCategoriaAnomaliaRequest();
        request.setNombre("Estenosis");
        request.setCodigo("est");
        when(categoriaRepository.existsByNombreIgnoreCase("Estenosis")).thenReturn(false);
        when(categoriaRepository.existsByCodigoIgnoreCase("EST")).thenReturn(false);

        String redirect = controller.create(request);

        assertEquals("redirect:/dashboard/categorias-anomalias?success=Categoria creada correctamente", redirect);
    }

    @Test
    void hu024_shouldValidateRequiredFieldsOnCreate() {
        CreateCategoriaAnomaliaRequest request = new CreateCategoriaAnomaliaRequest();
        request.setNombre(" ");
        request.setCodigo("EST");

        String redirect = controller.create(request);

        assertEquals("redirect:/dashboard/categorias-anomalias?error=El nombre es obligatorio", redirect);
    }

    @Test
    void hu024_shouldShowErrorWhenCreateFails() {
        CreateCategoriaAnomaliaRequest request = new CreateCategoriaAnomaliaRequest();
        request.setNombre("Estenosis");
        request.setCodigo("EST");
        when(categoriaRepository.existsByNombreIgnoreCase("Estenosis")).thenReturn(true);

        String redirect = controller.create(request);

        assertEquals("redirect:/dashboard/categorias-anomalias?error=Ya existe una categoria con ese nombre", redirect);
    }

    @Test
    void hu025_shouldUpdateCategorySuccessfully() {
        UpdateCategoriaAnomaliaRequest request = new UpdateCategoriaAnomaliaRequest();
        request.setNombre("Insuficiencia");
        request.setCodigo("INS");
        CategoriaAnomaliaEntity entity = buildCategoria(1L, "Estenosis", "EST");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(categoriaRepository.existsByNombreIgnoreCaseAndIdNot("Insuficiencia", 1L)).thenReturn(false);
        when(categoriaRepository.existsByCodigoIgnoreCaseAndIdNot("INS", 1L)).thenReturn(false);

        String redirect = controller.edit(1L, request);

        assertEquals("redirect:/dashboard/categorias-anomalias?success=Categoria actualizada correctamente", redirect);
    }

    @Test
    void hu025_shouldShowValidationMessageForInvalidData() {
        UpdateCategoriaAnomaliaRequest request = new UpdateCategoriaAnomaliaRequest();
        request.setNombre("   ");
        request.setCodigo("INS");
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(buildCategoria(1L, "Estenosis", "EST")));

        String redirect = controller.edit(1L, request);

        assertEquals("redirect:/dashboard/categorias-anomalias?error=El nombre es obligatorio", redirect);
    }

    @Test
    void hu025_shouldNotifyErrorWhenUpdateFails() {
        UpdateCategoriaAnomaliaRequest request = new UpdateCategoriaAnomaliaRequest();
        request.setNombre("Insuficiencia");
        request.setCodigo("INS");
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        String redirect = controller.edit(99L, request);

        assertEquals("redirect:/dashboard/categorias-anomalias?error=Categoria no encontrada", redirect);
    }

    @Test
    void hu026_shouldDeleteCategoryWhenConfirmed() {
        when(categoriaRepository.existsById(1L)).thenReturn(true);

        String redirect = controller.delete(1L);

        assertEquals("redirect:/dashboard/categorias-anomalias?success=Categoria eliminada correctamente", redirect);
    }

    @Test
    void hu026_shouldPreventDeleteWhenCategoryHasDependencies() {
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        doThrow(new DataIntegrityViolationException("FK violation")).when(categoriaRepository).deleteById(1L);

        String redirect = controller.delete(1L);

        assertEquals("redirect:/dashboard/categorias-anomalias?error=No se puede eliminar la categoria porque esta en uso", redirect);
    }

    @Test
    void hu026_shouldShowErrorWhenDeleteFails() {
        when(categoriaRepository.existsById(404L)).thenReturn(false);

        String redirect = controller.delete(404L);

        assertEquals("redirect:/dashboard/categorias-anomalias?error=Categoria no encontrada", redirect);
    }

    private CategoriaAnomaliaEntity buildCategoria(Long id, String nombre, String codigo) {
        CategoriaAnomaliaEntity entity = new CategoriaAnomaliaEntity();
        entity.setId(id);
        entity.setNombre(nombre);
        entity.setCodigo(codigo);
        return entity;
    }
}
