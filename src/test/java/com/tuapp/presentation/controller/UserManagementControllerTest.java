package com.tuapp.presentation.controller;

import com.tuapp.application.usecase.CreateUserUseCase;
import com.tuapp.application.usecase.DeleteUserUseCase;
import com.tuapp.application.usecase.GetAllUsersUseCase;
import com.tuapp.application.usecase.UpdateUserPasswordUseCase;
import com.tuapp.application.usecase.UpdateUserUseCase;
import com.tuapp.domain.model.User;
import com.tuapp.presentation.dto.CreateUserRequest;
import com.tuapp.presentation.dto.UpdateUserPasswordRequest;
import com.tuapp.presentation.dto.UpdateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserManagementControllerTest {

    @Mock
    private GetAllUsersUseCase getAllUsersUseCase;

    @Mock
    private CreateUserUseCase createUserUseCase;

    @Mock
    private UpdateUserUseCase updateUserUseCase;

    @Mock
    private DeleteUserUseCase deleteUserUseCase;

    @Mock
    private UpdateUserPasswordUseCase updateUserPasswordUseCase;

    private UserManagementController controller;

    @BeforeEach
    void setUp() {
        controller = new UserManagementController(
                getAllUsersUseCase,
                createUserUseCase,
                updateUserUseCase,
                deleteUserUseCase,
                updateUserPasswordUseCase
        );
    }

    @Test
    void hu035_shouldListUsersWhenAccessingModule() {
        User user = buildUser(1L, "admin", "admin@demo.com", "ADMIN");
        when(getAllUsersUseCase.execute()).thenReturn(List.of(user));
        Model model = new ExtendedModelMap();

        String view = controller.users(model, null, null);

        assertEquals("user-management", view);
        assertEquals(1, ((List<?>) model.getAttribute("users")).size());
    }

    @Test
    void hu035_shouldShowUsersWithRelevantInfo() {
        User admin = buildUser(1L, "admin", "admin@demo.com", "ADMIN");
        User user = buildUser(2L, "medico", "medico@demo.com", "USER");
        when(getAllUsersUseCase.execute()).thenReturn(List.of(admin, user));
        Model model = new ExtendedModelMap();

        controller.users(model, null, null);

        @SuppressWarnings("unchecked")
        List<User> users = (List<User>) model.getAttribute("users");
        assertEquals("admin", users.get(0).getNombreUsuario());
        assertEquals("ADMIN", users.get(0).getRoleName());
        assertEquals("medico", users.get(1).getNombreUsuario());
    }

    @Test
    void hu035_shouldHandleEmptyUsersList() {
        when(getAllUsersUseCase.execute()).thenReturn(List.of());
        Model model = new ExtendedModelMap();

        String view = controller.users(model, null, null);

        assertEquals("user-management", view);
        assertEquals(List.of(), model.getAttribute("users"));
    }

    @Test
    void hu036_shouldCreateUserSuccessfully() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("nuevo");
        request.setEmail("nuevo@demo.com");
        request.setPassword("Abcd1234!");
        request.setRoleName("USER");

        String redirect = controller.create(request);

        assertEquals("redirect:/dashboard/users?success=Usuario creado correctamente", redirect);
    }

    @Test
    void hu036_shouldValidateInvalidFieldsOnCreate() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("nuevo");
        request.setEmail("nuevo@demo.com");
        request.setPassword("123");
        request.setRoleName("USER");
        doThrow(new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres"))
                .when(createUserUseCase).execute(request);

        String redirect = controller.create(request);

        assertEquals("redirect:/dashboard/users?error=La contraseña debe tener al menos 8 caracteres", redirect);
    }

    @Test
    void hu036_shouldShowErrorWhenCreateFails() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("nuevo");
        request.setEmail("nuevo@demo.com");
        request.setPassword("Abcd1234!");
        request.setRoleName("USER");
        doThrow(new IllegalArgumentException("El email ya existe")).when(createUserUseCase).execute(request);

        String redirect = controller.create(request);

        assertEquals("redirect:/dashboard/users?error=El email ya existe", redirect);
    }

    @Test
    void hu037_shouldUpdateUserSuccessfully() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("admin2");
        request.setEmail("admin2@demo.com");
        request.setRoleName("ADMIN");

        String redirect = controller.edit(1L, request);

        assertEquals("redirect:/dashboard/users?success=Usuario actualizado correctamente", redirect);
    }

    @Test
    void hu037_shouldShowValidationMessageForInvalidData() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername(" ");
        request.setEmail("bad-email");
        request.setRoleName("ADMIN");
        doThrow(new IllegalArgumentException("Rol inválido")).when(updateUserUseCase).execute(1L, request);

        String redirect = controller.edit(1L, request);

        assertEquals("redirect:/dashboard/users?error=Rol inválido", redirect);
    }

    @Test
    void hu037_shouldNotifyErrorWhenUpdateFails() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("admin2");
        request.setEmail("admin2@demo.com");
        request.setRoleName("ADMIN");
        doThrow(new IllegalArgumentException("Usuario no encontrado")).when(updateUserUseCase).execute(99L, request);

        String redirect = controller.edit(99L, request);

        assertEquals("redirect:/dashboard/users?error=Usuario no encontrado", redirect);
    }

    @Test
    void hu038_shouldDeleteUserWhenConfirmed() {
        String redirect = controller.delete(1L);

        assertEquals("redirect:/dashboard/users?success=Usuario eliminado correctamente", redirect);
    }

    @Test
    void hu038_shouldPreventDeleteWhenUserHasAssociatedRecords() {
        doThrow(new IllegalArgumentException("El usuario tiene registros asociados"))
                .when(deleteUserUseCase).execute(1L);

        String redirect = controller.delete(1L);

        assertEquals("redirect:/dashboard/users?error=El usuario tiene registros asociados", redirect);
    }

    @Test
    void hu038_shouldShowErrorWhenDeleteFails() {
        doThrow(new IllegalArgumentException("Usuario no encontrado"))
                .when(deleteUserUseCase).execute(404L);

        String redirect = controller.delete(404L);

        assertEquals("redirect:/dashboard/users?error=Usuario no encontrado", redirect);
    }

    private User buildUser(Long id, String username, String email, String roleName) {
        return new User(id, username, email, "hash", 1L, roleName, LocalDateTime.now());
    }
}
