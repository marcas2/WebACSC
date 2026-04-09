package com.tuapp.application.usecase;

import com.tuapp.application.dto.RegisterRequest;
import com.tuapp.domain.model.User;
import com.tuapp.domain.service.AuthDomainService;
import com.tuapp.infrastructure.persistence.entity.RoleEntity;
import com.tuapp.infrastructure.persistence.repository.RoleJpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RegisterUseCase {

    private static final String DEFAULT_ROLE_NAME = "USER";

    private final AuthDomainService authDomainService;
    private final PasswordEncoder passwordEncoder;
    private final RoleJpaRepository roleRepository;

    public RegisterUseCase(AuthDomainService authDomainService,
                           PasswordEncoder passwordEncoder,
                           RoleJpaRepository roleRepository) {
        this.authDomainService = authDomainService;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public User execute(RegisterRequest request) {
        String username = request.getUsername() != null ? request.getUsername().trim() : "";
        String email = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";
        String rawPassword = request.getPassword();

        if (username.isBlank()) {
            throw new IllegalArgumentException("El usuario es requerido.");
        }

        if (email.isBlank()) {
            throw new IllegalArgumentException("El email es requerido.");
        }

        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("La contraseña es requerida.");
        }

        RoleEntity defaultRole = roleRepository.findByNombre(DEFAULT_ROLE_NAME)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró el rol por defecto para el registro."
                ));

        String hashedPassword = passwordEncoder.encode(rawPassword);

        User newUser = new User(
                null,
                username,
                email,
                hashedPassword,
                defaultRole.getId(),      // debería ser 3
                defaultRole.getNombre(),    // USER
                LocalDateTime.now()
        );

        return authDomainService.registerUser(newUser);
    }
}