package com.tuapp.domain.repository;

import java.util.Optional;

public interface RoleRepository {
    Optional<Long> findIdByName(String name);
}