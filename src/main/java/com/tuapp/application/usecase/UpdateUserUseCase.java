package com.tuapp.application.usecase;

import com.tuapp.domain.model.User;
import com.tuapp.domain.repository.RoleRepository;
import com.tuapp.domain.repository.UserRepository;
import com.tuapp.presentation.dto.UpdateUserRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateUserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UpdateUserUseCase(UserRepository userRepository,
                             RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public void execute(Long userId, UpdateUserRequest request) {
        User current = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        String username = request.getUsername().trim();
        String email = request.getEmail().trim().toLowerCase();
        String roleName = normalizeRole(request.getRoleName());

        if (userRepository.existsByUsernameAndIdNot(username, userId)) {
            throw new IllegalArgumentException("El username ya está en uso");
        }

        if (userRepository.existsByEmailAndIdNot(email, userId)) {
            throw new IllegalArgumentException("El email ya está en uso");
        }

        Long roleId = roleRepository.findIdByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));

        User updated = new User(
                current.getId(),
                username,
                email,
                current.getHashContrasena(),
                roleId,
                roleName,
                current.getCreadoEn()
        );

        userRepository.save(updated);
    }

    private String normalizeRole(String roleName) {
        String role = roleName == null ? "" : roleName.trim().toUpperCase();

        if (!role.equals("SUPER_USER") && !role.equals("ADMIN") && !role.equals("USER")) {
            throw new IllegalArgumentException("Rol inválido");
        }

        return role;
    }
}