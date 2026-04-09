package com.tuapp.infrastructure.persistence.repository;

import com.tuapp.domain.repository.RoleRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RoleRepositoryImpl implements RoleRepository {

    private final RoleJpaRepository roleJpaRepository;

    public RoleRepositoryImpl(RoleJpaRepository roleJpaRepository) {
        this.roleJpaRepository = roleJpaRepository;
    }

    @Override
    public Optional<Long> findIdByName(String name) {
        return roleJpaRepository.findByNombre(name).map(role -> role.getId());
    }
}