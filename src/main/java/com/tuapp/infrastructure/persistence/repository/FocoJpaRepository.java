package com.tuapp.infrastructure.persistence.repository;

import com.tuapp.infrastructure.persistence.entity.FocoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FocoJpaRepository extends JpaRepository<FocoEntity, Long> {
}
