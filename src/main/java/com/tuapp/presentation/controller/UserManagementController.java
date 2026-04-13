package com.tuapp.presentation.controller;

import com.tuapp.application.usecase.CreateUserUseCase;
import com.tuapp.application.usecase.DeleteUserUseCase;
import com.tuapp.application.usecase.GetAllUsersUseCase;
import com.tuapp.application.usecase.UpdateUserPasswordUseCase;
import com.tuapp.application.usecase.UpdateUserUseCase;
import com.tuapp.presentation.dto.CreateUserRequest;
import com.tuapp.presentation.dto.UpdateUserPasswordRequest;
import com.tuapp.presentation.dto.UpdateUserRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/dashboard/users")
public class UserManagementController {

    private final GetAllUsersUseCase getAllUsersUseCase;
    private final CreateUserUseCase createUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final UpdateUserPasswordUseCase updateUserPasswordUseCase;

    public UserManagementController(GetAllUsersUseCase getAllUsersUseCase,
                                    CreateUserUseCase createUserUseCase,
                                    UpdateUserUseCase updateUserUseCase,
                                    DeleteUserUseCase deleteUserUseCase,
                                    UpdateUserPasswordUseCase updateUserPasswordUseCase) {
        this.getAllUsersUseCase = getAllUsersUseCase;
        this.createUserUseCase = createUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
        this.updateUserPasswordUseCase = updateUserPasswordUseCase;
    }

    @GetMapping
    public String users(Model model,
                        @RequestParam(required = false) String success,
                        @RequestParam(required = false) String error,
                        @RequestParam(required = false) Long pendingDeleteUserId) {
        model.addAttribute("users", getAllUsersUseCase.execute());
        model.addAttribute("success", success);
        model.addAttribute("error", error);
        model.addAttribute("pendingDeleteUserId", pendingDeleteUserId);
        return "user-management";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute CreateUserRequest request,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return redirectWithError(bindingResult.getFieldErrors().get(0).getDefaultMessage());
        }

        try {
            createUserUseCase.execute(request);
            return "redirect:/dashboard/users?success=Usuario creado correctamente";
        } catch (IllegalArgumentException ex) {
            return redirectWithError(ex.getMessage());
        } catch (RuntimeException ex) {
            return redirectWithError("Error inesperado al crear el usuario");
        }
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                       @Valid @ModelAttribute UpdateUserRequest request,
                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return redirectWithError(bindingResult.getFieldErrors().get(0).getDefaultMessage());
        }

        try {
            updateUserUseCase.execute(id, request);
            return "redirect:/dashboard/users?success=Usuario actualizado correctamente";
        } catch (IllegalArgumentException ex) {
            return redirectWithError(ex.getMessage());
        } catch (RuntimeException ex) {
            return redirectWithError("Error inesperado al actualizar el usuario");
        }
    }

    @PostMapping("/{id}/password")
    public String updatePassword(@PathVariable Long id,
                                 @Valid @ModelAttribute UpdateUserPasswordRequest request,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return redirectWithError(bindingResult.getFieldErrors().get(0).getDefaultMessage());
        }

        try {
            updateUserPasswordUseCase.execute(id, request.getNewPassword());
            return "redirect:/dashboard/users?success=Contraseña actualizada correctamente";
        } catch (IllegalArgumentException ex) {
            return redirectWithError(ex.getMessage());
        } catch (RuntimeException ex) {
            return redirectWithError("Error inesperado al cambiar la contraseña");
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         @RequestParam(defaultValue = "false") boolean deleteDiagnostics) {
        try {
            deleteUserUseCase.execute(id, deleteDiagnostics);
            if (deleteDiagnostics) {
                return "redirect:/dashboard/users?success=Usuario y diagnósticos eliminados correctamente";
            }

            return "redirect:/dashboard/users?success=Usuario eliminado correctamente";
        } catch (IllegalArgumentException ex) {
            if (!deleteDiagnostics && "El usuario tiene diagnósticos asociados".equals(ex.getMessage())) {
                return "redirect:/dashboard/users?error=" + ex.getMessage() + "&pendingDeleteUserId=" + id;
            }

            return redirectWithError(ex.getMessage());
        } catch (RuntimeException ex) {
            return redirectWithError("Error inesperado al eliminar el usuario");
        }
    }

    private String redirectWithError(String message) {
        return "redirect:/dashboard/users?error=" + message;
    }
}