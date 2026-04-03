package com.tuapp.domain.service;

import com.tuapp.domain.model.User;
import com.tuapp.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthDomainService {

    private final UserRepository userRepository;

    public AuthDomainService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
    }

    public User registerUser(User user) {
        if (usernameExists(user.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }
        if (emailExists(user.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        return userRepository.save(user);
    }
}