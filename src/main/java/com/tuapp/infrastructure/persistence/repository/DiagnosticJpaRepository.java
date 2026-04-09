package com.tuapp.infrastructure.persistence.repository;

import com.tuapp.infrastructure.persistence.entity.DiagnosticEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DiagnosticJpaRepository extends JpaRepository<DiagnosticEntity, Long> {

    @Query("""
        SELECT d
        FROM DiagnosticEntity d
        LEFT JOIN FETCH d.usuarioCrea uc
        ORDER BY d.creadoEn DESC, d.id DESC
    """)
    List<DiagnosticEntity> findAllWithUsuarioCrea();

    @Query("""
        SELECT d
        FROM DiagnosticEntity d
        LEFT JOIN FETCH d.usuarioCrea uc
        WHERE uc.id = :creatorId
        ORDER BY d.creadoEn DESC, d.id DESC
    """)
    List<DiagnosticEntity> findAllByUsuarioCreaIdWithUsuarioCrea(Long creatorId);

    @Query("""
        SELECT
            CASE
                WHEN d.edad BETWEEN 0 AND 17 THEN '0-17'
                WHEN d.edad BETWEEN 18 AND 35 THEN '18-35'
                WHEN d.edad BETWEEN 36 AND 59 THEN '36-59'
                ELSE '60+'
            END,
            COUNT(d)
        FROM DiagnosticEntity d
        WHERE d.esNormal = false
        GROUP BY
            CASE
                WHEN d.edad BETWEEN 0 AND 17 THEN '0-17'
                WHEN d.edad BETWEEN 18 AND 35 THEN '18-35'
                WHEN d.edad BETWEEN 36 AND 59 THEN '36-59'
                ELSE '60+'
            END
        ORDER BY 1
    """)
    List<Object[]> countValvulopathiesByAgeRange();

    @Query("""
        SELECT d.genero, COUNT(d)
        FROM DiagnosticEntity d
        WHERE d.esNormal = false
        GROUP BY d.genero
        ORDER BY d.genero
    """)
    List<Object[]> countValvulopathiesByGender();

    @Query("""
        SELECT
            CASE
                WHEN eb.id IS NULL THEN 'SIN ENFERMEDAD DE BASE'
                ELSE 'CON ENFERMEDAD DE BASE'
            END,
            COUNT(DISTINCT d.id)
        FROM DiagnosticEntity d
        LEFT JOIN d.enfermedadesBase eb
        WHERE d.esNormal = false
        GROUP BY
            CASE
                WHEN eb.id IS NULL THEN 'SIN ENFERMEDAD DE BASE'
                ELSE 'CON ENFERMEDAD DE BASE'
            END
        ORDER BY 1
    """)
    List<Object[]> countValvulopathiesByUnderlyingDiseases();

    @Query("""
        SELECT
            CASE
                WHEN d.esNormal = true THEN 'NORMAL'
                ELSE 'ANORMAL'
            END,
            COUNT(d)
        FROM DiagnosticEntity d
        GROUP BY
            CASE
                WHEN d.esNormal = true THEN 'NORMAL'
                ELSE 'ANORMAL'
            END
        ORDER BY 1
    """)
    List<Object[]> countByNormalStatus();

    @Query("""
        SELECT c.nombre, COUNT(d)
        FROM DiagnosticEntity d
        JOIN d.categoriaAnomalia c
        GROUP BY c.nombre
        ORDER BY c.nombre
    """)
    List<Object[]> countByCategoriaAnomalia();

    @Query("""
        SELECT f.nombre, COUNT(d)
        FROM DiagnosticEntity d
        JOIN d.foco f
        GROUP BY f.nombre
        ORDER BY f.nombre
    """)
    List<Object[]> countByFoco();

    @Query("""
        SELECT d.institucion, COUNT(d)
        FROM DiagnosticEntity d
        GROUP BY d.institucion
        ORDER BY d.institucion
    """)
    List<Object[]> countByInstitucion();

    @Query(value = """
        SELECT to_char(date_trunc('month', d.created_at), 'YYYY-MM') AS periodo, COUNT(*)
        FROM diagnostics d
        GROUP BY date_trunc('month', d.created_at)
        ORDER BY date_trunc('month', d.created_at)
    """, nativeQuery = true)
    List<Object[]> countByMonth();
}