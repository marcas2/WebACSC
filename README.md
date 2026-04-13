# WebACSC

## Requisitos para correr con Docker

1. Docker Desktop instalado y en ejecucion.
2. Docker Compose v2 habilitado (`docker compose version`).
3. Puerto `4000` libre para la aplicacion.
4. PostgreSQL ya desplegado y accesible desde el contenedor de la app.

## Archivo APK

Si quieres habilitar la descarga desde el dashboard:

1. Coloca el archivo en `apk/acsc.apk`.
2. La app lo expone en `GET /dashboard/apk/download`.

## Despliegue rapido

Desde la raiz del proyecto:

```bash
docker compose build app
docker compose up -d app
```

Si tu base no esta en `host.docker.internal:5520`, levanta con variables:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://TU_HOST_DB:TU_PUERTO/dashboard_db \
SPRING_DATASOURCE_USERNAME=TU_USUARIO \
SPRING_DATASOURCE_PASSWORD=TU_PASSWORD \
docker compose up -d --build
```

En PowerShell (Windows):

```powershell
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://TU_HOST_DB:TU_PUERTO/dashboard_db"
$env:SPRING_DATASOURCE_USERNAME="TU_USUARIO"
$env:SPRING_DATASOURCE_PASSWORD="TU_PASSWORD"
docker compose up -d --build app
```

Ver logs:

```bash
docker compose logs -f app
```

Detener todo:

```bash
docker compose down
```

## Variables importantes (ya definidas en docker-compose)

- `SPRING_DATASOURCE_URL` (default: `jdbc:postgresql://host.docker.internal:5520/dashboard_db`)
- `SPRING_DATASOURCE_USERNAME` (default: `postgres`)
- `SPRING_DATASOURCE_PASSWORD` (default: `postgres`)
- `APP_JWT_SECRET` (debe ser base64 y segura en produccion)
- `APP_APK_RESOURCE_PATH=file:/app/apk/acsc.apk`

## URLs

- App: `http://localhost:4000`
- Descarga APK: `http://localhost:4000/dashboard/apk/download`