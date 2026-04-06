package com.tuapp.infrastructure.persistence.mapper;

import com.tuapp.domain.model.User;
import com.tuapp.infrastructure.persistence.entity.RoleEntity;
import com.tuapp.infrastructure.persistence.entity.UserEntity;
import com.tuapp.infrastructure.persistence.repository.RoleJpaRepository;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final RoleJpaRepository roleRepository;

    public UserMapper(RoleJpaRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public User toDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getRole().getId(),
                entity.getRole().getName(),
                entity.getCreatedAt()
        );
    }

    public UserEntity toEntity(User domain) {
        UserEntity entity = new UserEntity();
        entity.setId(domain.getId());
        entity.setUsername(domain.getUsername());
        entity.setEmail(domain.getEmail());
        entity.setPasswordHash(domain.getPasswordHash());
        entity.setCreatedAt(domain.getCreatedAt());

        RoleEntity role = resolveRole(domain);
        entity.setRole(role);

        return entity;
    }

    private RoleEntity resolveRole(User domain) {
        if (domain.getRoleId() != null) {
            return roleRepository.findById(domain.getRoleId())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado con id: " + domain.getRoleId()));
        }

        if (domain.getRoleName() != null && !domain.getRoleName().trim().isEmpty()) {
            return roleRepository.findByName(domain.getRoleName().trim().toUpperCase())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado con nombre: " + domain.getRoleName()));
        }

        throw new RuntimeException("El usuario no tiene roleId ni roleName para resolver el rol");
    }
}