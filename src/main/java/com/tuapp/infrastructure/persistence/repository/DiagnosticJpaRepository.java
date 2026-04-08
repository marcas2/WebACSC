package com.tuapp.infrastructure.persistence.repository;

import com.tuapp.infrastructure.persistence.entity.DiagnosticEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DiagnosticJpaRepository extends JpaRepository<DiagnosticEntity, Long> {

    @Query("""
        SELECT
            CASE
                WHEN d.age BETWEEN 0 AND 17 THEN '0-17'
                WHEN d.age BETWEEN 18 AND 35 THEN '18-35'
                WHEN d.age BETWEEN 36 AND 59 THEN '36-59'
                ELSE '60+'
            END,
            COUNT(d)
        FROM DiagnosticEntity d
        WHERE d.isNormal = false
        GROUP BY
            CASE
                WHEN d.age BETWEEN 0 AND 17 THEN '0-17'
                WHEN d.age BETWEEN 18 AND 35 THEN '18-35'
                WHEN d.age BETWEEN 36 AND 59 THEN '36-59'
                ELSE '60+'
            END
        ORDER BY
            CASE
                WHEN CASE
                    WHEN d.age BETWEEN 0 AND 17 THEN '0-17'
                    WHEN d.age BETWEEN 18 AND 35 THEN '18-35'
                    WHEN d.age BETWEEN 36 AND 59 THEN '36-59'
                    ELSE '60+'
                END = '0-17' THEN 1
                WHEN CASE
                    WHEN d.age BETWEEN 0 AND 17 THEN '0-17'
                    WHEN d.age BETWEEN 18 AND 35 THEN '18-35'
                    WHEN d.age BETWEEN 36 AND 59 THEN '36-59'
                    ELSE '60+'
                END = '18-35' THEN 2
                WHEN CASE
                    WHEN d.age BETWEEN 0 AND 17 THEN '0-17'
                    WHEN d.age BETWEEN 18 AND 35 THEN '18-35'
                    WHEN d.age BETWEEN 36 AND 59 THEN '36-59'
                    ELSE '60+'
                END = '36-59' THEN 3
                ELSE 4
            END
    """)
    List<Object[]> countValvulopathiesByAgeRange();

    @Query("""
        SELECT d.gender, COUNT(d)
        FROM DiagnosticEntity d
        WHERE d.isNormal = false
        GROUP BY d.gender
        ORDER BY d.gender
    """)
    List<Object[]> countValvulopathiesByGender();

    @Query("""
        SELECT
            CASE
                WHEN d.enfermedadesBase IS EMPTY THEN 'SIN ENFERMEDAD DE BASE'
                ELSE 'CON ENFERMEDAD DE BASE'
            END,
            COUNT(d)
        FROM DiagnosticEntity d
        WHERE d.isNormal = false
        GROUP BY
            CASE
                WHEN d.enfermedadesBase IS EMPTY THEN 'SIN ENFERMEDAD DE BASE'
                ELSE 'CON ENFERMEDAD DE BASE'
            END
    """)
    List<Object[]> countValvulopathiesByUnderlyingDiseases();
    // Añadir este método al repositorio existente:
    long countByIsNormalFalse();
}