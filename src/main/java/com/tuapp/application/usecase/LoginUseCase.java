package com.tuapp.application.usecase;

import com.tuapp.application.dto.AuthResponse;
import com.tuapp.application.dto.LoginRequest;
import com.tuapp.domain.model.User;
import com.tuapp.domain.service.AuthDomainService;
import com.tuapp.infrastructure.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginUseCase {

    private final AuthDomainService authDomainService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginUseCase(AuthDomainService authDomainService,
                        PasswordEncoder passwordEncoder,
                        JwtTokenProvider jwtTokenProvider) {
        this.authDomainService = authDomainService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public AuthResponse execute(LoginRequest request) {
        User user = authDomainService.findByUsername(request.getUsername());

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        String token = jwtTokenProvider.generateToken(
                user.getUsername(),
                user.getRoleName()
        );

        return new AuthResponse(
                token,
                user.getUsername(),
                user.getRoleName()
        );
    }
}