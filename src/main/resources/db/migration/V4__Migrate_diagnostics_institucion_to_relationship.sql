ALTER TABLE diagnostics
    ADD COLUMN IF NOT EXISTS institucion_id BIGINT;

DO $$
BEGIN
        IF EXISTS (
                SELECT 1
                FROM information_schema.columns
                WHERE table_name = 'diagnostics'
                    AND column_name = 'institucion'
        ) THEN
                -- Crea instituciones faltantes a partir del valor texto histórico en diagnostics.institucion
                INSERT INTO instituciones (nombre, activo, creado_en)
                SELECT DISTINCT TRIM(d.institucion), true, NOW()
                FROM diagnostics d
                WHERE d.institucion IS NOT NULL
                    AND LENGTH(TRIM(d.institucion)) > 0
                    AND NOT EXISTS (
                            SELECT 1
                            FROM instituciones i
                            WHERE LOWER(i.nombre) = LOWER(TRIM(d.institucion))
                    );

                -- Vincula cada diagnóstico con su institución por nombre (ignorando mayúsculas/minúsculas)
                UPDATE diagnostics d
                SET institucion_id = i.id
                FROM instituciones i
                WHERE d.institucion_id IS NULL
                    AND d.institucion IS NOT NULL
                    AND LOWER(TRIM(d.institucion)) = LOWER(i.nombre);
        END IF;
END $$;

-- Asigna una institución por defecto si algún registro quedó sin vínculo
INSERT INTO instituciones (nombre, activo, creado_en)
SELECT 'SIN INSTITUCION', true, NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM instituciones WHERE nombre = 'SIN INSTITUCION'
);

UPDATE diagnostics d
SET institucion_id = (
    SELECT id FROM instituciones WHERE nombre = 'SIN INSTITUCION' ORDER BY id LIMIT 1
)
WHERE d.institucion_id IS NULL;

ALTER TABLE diagnostics
    ALTER COLUMN institucion_id SET NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_diagnostics_institucion'
    ) THEN
        ALTER TABLE diagnostics
            ADD CONSTRAINT fk_diagnostics_institucion
            FOREIGN KEY (institucion_id) REFERENCES instituciones(id);
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_diagnostics_institucion
    ON diagnostics(institucion_id);

ALTER TABLE diagnostics
    DROP COLUMN IF EXISTS institucion;
