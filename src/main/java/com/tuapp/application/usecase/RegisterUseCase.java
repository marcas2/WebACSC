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
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        RoleEntity defaultRole = roleRepository.findByName("USUARIO")
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        User newUser = new User(
                null,
                request.getUsername(),
                request.getEmail(),
                hashedPassword,
                defaultRole.getId(),
                defaultRole.getName(),
                LocalDateTime.now()
        );

        return authDomainService.registerUser(newUser);
    }
}