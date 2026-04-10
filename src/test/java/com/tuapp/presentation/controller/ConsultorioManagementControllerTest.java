package com.tuapp.presentation.controller;

import com.tuapp.infrastructure.persistence.entity.ConsultorioEntity;
import com.tuapp.infrastructure.persistence.entity.InstitucionEntity;
import com.tuapp.infrastructure.persistence.repository.ConsultorioJpaRepository;
import com.tuapp.infrastructure.persistence.repository.InstitucionJpaRepository;
import com.tuapp.presentation.dto.CreateConsultorioRequest;
import com.tuapp.presentation.dto.UpdateConsultorioRequest;
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
class ConsultorioManagementControllerTest {

    @Mock
    private ConsultorioJpaRepository consultorioRepository;

    @Mock
    private InstitucionJpaRepository institucionRepository;

    private ConsultorioManagementController controller;

    @BeforeEach
    void setUp() {
        controller = new ConsultorioManagementController(consultorioRepository, institucionRepository);
    }

    @Test
    void hu019_shouldShowConsultoriosByInstitution() {
        InstitucionEntity institucion = buildInstitucion(1L, "Hospital Norte", true);
        ConsultorioEntity consultorio = buildConsultorio(10L, "Consultorio A", "A-01", true, institucion);
        when(institucionRepository.findById(1L)).thenReturn(Optional.of(institucion));
        when(consultorioRepository.findByInstitucionIdOrderByNombreAsc(1L)).thenReturn(List.of(consultorio));
        Model model = new ExtendedModelMap();

        String view = controller.list(1L, model, null, null);

        assertEquals("consultorio-management", view);
        assertEquals("Hospital Norte", ((InstitucionEntity) model.getAttribute("institucion")).getNombre());
    }

    @Test
    void hu019_shouldListConsultoriosWhenTheyExist() {
        InstitucionEntity institucion = buildInstitucion(1L, "Hospital Norte", true);
        ConsultorioEntity a = buildConsultorio(10L, "Consultorio A", "A-01", true, institucion);
        ConsultorioEntity b = buildConsultorio(11L, "Consultorio B", "B-01", true, institucion);
        when(institucionRepository.findById(1L)).thenReturn(Optional.of(institucion));
        when(consultorioRepository.findByInstitucionIdOrderByNombreAsc(1L)).thenReturn(List.of(a, b));
        Model model = new ExtendedModelMap();

        controller.list(1L, model, null, null);

        assertEquals(2, ((List<?>) model.getAttribute("consultorios")).size());
    }

    @Test
    void hu019_shouldHandleNoConsultoriosForInstitution() {
        InstitucionEntity institucion = buildInstitucion(1L, "Hospital Norte", true);
        when(institucionRepository.findById(1L)).thenReturn(Optional.of(institucion));
        when(consultorioRepository.findByInstitucionIdOrderByNombreAsc(1L)).thenReturn(List.of());
        Model model = new ExtendedModelMap();

        String view = controller.list(1L, model, null, null);

        assertEquals("consultorio-management", view);
        assertEquals(List.of(), model.getAttribute("consultorios"));
    }

    @Test
    void hu020_shouldCreateConsultorioSuccessfully() {
        InstitucionEntity institucion = buildInstitucion(1L, "Hospital Norte", true);
        CreateConsultorioRequest request = new CreateConsultorioRequest();
        request.setNombre("Consultorio A");
        request.setCodigo("a-01");

        when(institucionRepository.findById(1L)).thenReturn(Optional.of(institucion));
        when(consultorioRepository.existsByCodigoIgnoreCase("A-01")).thenReturn(false);
        when(consultorioRepository.existsByNombreIgnoreCaseAndInstitucionId("Consultorio A", 1L)).thenReturn(false);

        String redirect = controller.create(1L, request);

        assertEquals("redirect:/dashboard/instituciones/1/consultorios?success=Consultorio creado correctamente", redirect);
    }

    @Test
    void hu020_shouldValidateRequiredFieldsOnCreate() {
        InstitucionEntity institucion = buildInstitucion(1L, "Hospital Norte", true);
        CreateConsultorioRequest request = new CreateConsultorioRequest();
        request.setNombre(" ");
        request.setCodigo(" ");
        when(institucionRepository.findById(1L)).thenReturn(Optional.of(institucion));

        String redirect = controller.create(1L, request);

        assertEquals("redirect:/dashboard/instituciones/1/consultorios?error=El nombre es obligatorio", redirect);
    }

    @Test
    void hu020_shouldNotifyErrorWhenCreateFails() {
        InstitucionEntity institucion = buildInstitucion(1L, "Hospital Norte", true);
        CreateConsultorioRequest request = new CreateConsultorioRequest();
        request.setNombre("Consultorio A");
        request.setCodigo("A-01");

        when(institucionRepository.findById(1L)).thenReturn(Optional.of(institucion));
        when(consultorioRepository.existsByCodigoIgnoreCase("A-01")).thenReturn(true);

        String redirect = controller.create(1L, request);

        assertEquals("redirect:/dashboard/instituciones/1/consultorios?error=Ya existe un consultorio con ese codigo", redirect);
    }

    @Test
    void hu021_shouldUpdateConsultorioSuccessfully() {
        InstitucionEntity institucion = buildInstitucion(1L, "Hospital Norte", true);
        ConsultorioEntity entity = buildConsultorio(10L, "Consultorio A", "A-01", true, institucion);
        UpdateConsultorioRequest request = new UpdateConsultorioRequest();
        request.setNombre("Consultorio B");
        request.setCodigo("B-02");
        request.setActivo(false);

        when(consultorioRepository.findById(10L)).thenReturn(Optional.of(entity));
        when(consultorioRepository.existsByCodigoIgnoreCaseAndIdNot("B-02", 10L)).thenReturn(false);
        when(consultorioRepository.existsByNombreIgnoreCaseAndInstitucionIdAndIdNot("Consultorio B", 1L, 10L)).thenReturn(false);

        String redirect = controller.edit(1L, 10L, request);

        assertEquals("redirect:/dashboard/instituciones/1/consultorios?success=Consultorio actualizado correctamente", redirect);
    }

    @Test
    void hu021_shouldShowValidationMessageForInvalidData() {
        InstitucionEntity institucion = buildInstitucion(1L, "Hospital Norte", true);
        ConsultorioEntity entity = buildConsultorio(10L, "Consultorio A", "A-01", true, institucion);
        UpdateConsultorioRequest request = new UpdateConsultorioRequest();
        request.setNombre(" ");
        request.setCodigo("B-02");
        request.setActivo(true);

        when(consultorioRepository.findById(10L)).thenReturn(Optional.of(entity));

        String redirect = controller.edit(1L, 10L, request);

        assertEquals("redirect:/dashboard/instituciones/1/consultorios?error=El nombre es obligatorio", redirect);
    }

    @Test
    void hu021_shouldNotifyErrorWhenUpdateFails() {
        UpdateConsultorioRequest request = new UpdateConsultorioRequest();
        request.setNombre("Consultorio X");
        request.setCodigo("X-01");
        request.setActivo(true);

        when(consultorioRepository.findById(404L)).thenReturn(Optional.empty());

        String redirect = controller.edit(1L, 404L, request);

        assertEquals("redirect:/dashboard/instituciones/1/consultorios?error=Consultorio no encontrado", redirect);
    }

    @Test
    void hu022_shouldDeleteConsultorioWhenConfirmed() {
        InstitucionEntity institucion = buildInstitucion(1L, "Hospital Norte", true);
        ConsultorioEntity entity = buildConsultorio(10L, "Consultorio A", "A-01", true, institucion);
        when(consultorioRepository.findById(10L)).thenReturn(Optional.of(entity));

        String redirect = controller.delete(1L, 10L);

        assertEquals("redirect:/dashboard/instituciones/1/consultorios?success=Consultorio eliminado correctamente", redirect);
    }

    @Test
    void hu022_shouldPreventDeleteWhenConsultorioHasAssociatedRecords() {
        InstitucionEntity institucion = buildInstitucion(1L, "Hospital Norte", true);
        ConsultorioEntity entity = buildConsultorio(10L, "Consultorio A", "A-01", true, institucion);
        when(consultorioRepository.findById(10L)).thenReturn(Optional.of(entity));
        doThrow(new IllegalArgumentException("El consultorio tiene registros asociados"))
                .when(consultorioRepository).delete(entity);

        String redirect = controller.delete(1L, 10L);

        assertEquals("redirect:/dashboard/instituciones/1/consultorios?error=El consultorio tiene registros asociados", redirect);
    }

    @Test
    void hu022_shouldShowErrorWhenDeleteFails() {
        InstitucionEntity otraInstitucion = buildInstitucion(2L, "Hospital Sur", true);
        ConsultorioEntity entity = buildConsultorio(10L, "Consultorio A", "A-01", true, otraInstitucion);
        when(consultorioRepository.findById(10L)).thenReturn(Optional.of(entity));

        String redirect = controller.delete(1L, 10L);

        assertEquals("redirect:/dashboard/instituciones/1/consultorios?error=El consultorio no pertenece a la institucion seleccionada", redirect);
    }

    private InstitucionEntity buildInstitucion(Long id, String nombre, Boolean activo) {
        InstitucionEntity entity = new InstitucionEntity();
        entity.setId(id);
        entity.setNombre(nombre);
        entity.setActivo(activo);
        return entity;
    }

    private ConsultorioEntity buildConsultorio(Long id, String nombre, String codigo, Boolean activo, InstitucionEntity institucion) {
        ConsultorioEntity entity = new ConsultorioEntity();
        entity.setId(id);
        entity.setNombre(nombre);
        entity.setCodigo(codigo);
        entity.setActivo(activo);
        entity.setInstitucion(institucion);
        return entity;
    }
}
