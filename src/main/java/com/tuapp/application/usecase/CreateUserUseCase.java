package com.tuapp.application.usecase;

import com.tuapp.domain.model.User;
import com.tuapp.domain.repository.RoleRepository;
import com.tuapp.domain.repository.UserRepository;
import com.tuapp.presentation.dto.CreateUserRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CreateUserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public CreateUserUseCase(UserRepository userRepository,
                             RoleRepository roleRepository,
                             PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void execute(CreateUserRequest request) {
        String username = request.getUsername().trim();
        String email = request.getEmail().trim().toLowerCase();
        String roleName = normalizeRole(request.getRoleName());

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("El username ya existe");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("El email ya existe");
        }

        validatePassword(request.getPassword());

        Long roleId = roleRepository.findIdByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));

        User user = new User(
                null,
                username,
                email,
                passwordEncoder.encode(request.getPassword()),
                roleId,
                roleName,
                LocalDateTime.now()
        );

        userRepository.save(user);
    }

    private String normalizeRole(String roleName) {
        String role = roleName == null ? "" : roleName.trim().toUpperCase();

        if (!role.equals("SUPER_USER") && !role.equals("ADMIN") && !role.equals("USER")) {
            throw new IllegalArgumentException("Rol inválido");
        }

        return role;
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
        }

        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(ch -> !Character.isLetterOrDigit(ch));

        if (!hasUpper || !hasLower || !hasDigit || !hasSpecial) {
            throw new IllegalArgumentException(
                    "La contraseña debe incluir mayúscula, minúscula, número y carácter especial"
            );
        }
    }
}