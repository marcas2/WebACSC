package com.tuapp.infrastructure.persistence.repository;

import com.tuapp.domain.model.User;
import com.tuapp.domain.repository.UserRepository;
import com.tuapp.infrastructure.persistence.entity.UserEntity;
import com.tuapp.infrastructure.persistence.mapper.UserMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;
    private final UserMapper mapper;

    public UserRepositoryImpl(UserJpaRepository jpaRepository, UserMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaRepository.findByNombreUsuario(username).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByNombreUsuario(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsernameAndIdNot(String username, Long id) {
        return jpaRepository.existsByNombreUsuarioAndIdNot(username, id);
    }

    @Override
    public boolean existsByEmailAndIdNot(String email, Long id) {
        return jpaRepository.existsByEmailAndIdNot(email, id);
    }

    @Override
    public List<User> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public User save(User user) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(user)));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!jpaRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        jpaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updatePassword(Long userId, String passwordHash) {
        UserEntity entity = jpaRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        entity.setHashContrasena(passwordHash);
        jpaRepository.save(entity);
    }
}