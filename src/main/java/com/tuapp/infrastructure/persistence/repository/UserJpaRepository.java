package com.tuapp.infrastructure.persistence.repository;

import com.tuapp.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByNombreUsuario(String nombreUsuario);
    Optional<UserEntity> findByEmail(String email);

    boolean existsByNombreUsuario(String nombreUsuario);
    boolean existsByEmail(String email);

    boolean existsByNombreUsuarioAndIdNot(String nombreUsuario, Long id);
    boolean existsByEmailAndIdNot(String email, Long id);
}