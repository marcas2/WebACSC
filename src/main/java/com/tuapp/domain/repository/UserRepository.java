package com.tuapp.domain.repository;

import com.tuapp.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsernameAndIdNot(String username, Long id);
    boolean existsByEmailAndIdNot(String email, Long id);

    List<User> findAll();

    User save(User user);
    void deleteById(Long id);

    void updatePassword(Long userId, String passwordHash);
}