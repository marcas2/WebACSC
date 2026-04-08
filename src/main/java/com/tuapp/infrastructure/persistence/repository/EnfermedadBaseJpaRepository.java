package com.tuapp.infrastructure.persistence.repository;

import com.tuapp.infrastructure.persistence.entity.EnfermedadBaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnfermedadBaseJpaRepository extends JpaRepository<EnfermedadBaseEntity, Long> {
}
