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
                entity.getNombreUsuario(),
                entity.getEmail(),
                entity.getHashContrasena(),
                entity.getRol().getId(),
                entity.getRol().getNombre(),
                entity.getCreadoEn()
        );
    }

    public UserEntity toEntity(User domain) {
        UserEntity entity = new UserEntity();
        entity.setId(domain.getId());
        entity.setNombreUsuario(domain.getNombreUsuario());
        entity.setEmail(domain.getEmail());
        entity.setHashContrasena(domain.getHashContrasena());
        entity.setCreadoEn(domain.getCreadoEn());

        RoleEntity role = resolveRole(domain);
        entity.setRol(role);

        return entity;
    }

    private RoleEntity resolveRole(User domain) {
        if (domain.getRoleId() != null) {
            return roleRepository.findById(domain.getRoleId())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado con id: " + domain.getRoleId()));
        }

        if (domain.getRoleName() != null && !domain.getRoleName().trim().isEmpty()) {
            return roleRepository.findByNombre(domain.getRoleName().trim().toUpperCase())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado con nombre: " + domain.getRoleName()));
        }

        throw new RuntimeException("El usuario no tiene roleId ni roleName para resolver el rol");
    }
}