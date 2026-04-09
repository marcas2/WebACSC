-- Renombrar columnas a español en tabla users
ALTER TABLE users RENAME COLUMN username TO nombreUsuario;

-- Renombrar columnas a español en tabla roles
ALTER TABLE roles RENAME COLUMN name TO nombre;

-- Renombrar columnas a español en tabla diagnostics
ALTER TABLE diagnostics RENAME COLUMN age TO edad;
ALTER TABLE diagnostics RENAME COLUMN gender TO genero;
ALTER TABLE diagnostics RENAME COLUMN is_normal TO esNormal;
