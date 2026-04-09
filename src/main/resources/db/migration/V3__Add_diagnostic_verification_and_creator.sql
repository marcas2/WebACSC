ALTER TABLE diagnostics
    ADD COLUMN IF NOT EXISTS verificado BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE diagnostics
    ADD COLUMN IF NOT EXISTS valvulopatia BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE diagnostics
    ADD COLUMN IF NOT EXISTS usuario_crea_id BIGINT;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_diagnostics_usuario_crea'
    ) THEN
        ALTER TABLE diagnostics
            ADD CONSTRAINT fk_diagnostics_usuario_crea
            FOREIGN KEY (usuario_crea_id) REFERENCES users(id);
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_diagnostics_usuario_crea
    ON diagnostics(usuario_crea_id);