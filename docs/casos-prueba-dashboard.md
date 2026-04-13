## Casos de prueba de filtros y consultas del dashboard

### Cobertura funcional

- HU4: cantidad de pacientes con valvulopatias por rango de edad.
- HU5: cantidad de pacientes con valvulopatias por genero.
- HU6: cantidad de pacientes con valvulopatias con y sin enfermedad de base.
- HU7: exportacion de resultados en PDF y Excel.
- HU9: total general de registros.
- HU10: proporcion normal/anormal.
- HU11: cantidad por tipo de anomalia.
- HU12: cantidad por foco de auscultacion.
- HU13: cantidad por institucion.
- HU14: evolucion mensual de registros.

### Filtros habilitados

- Texto diagnostico (`q`)
- Institucion (`institucionId`)
- Foco (`focoId`)
- Categoria de anomalia (`categoriaId`)
- Genero (`genero`)
- Edad minima y maxima (`edadMin`, `edadMax`)
- Rango de fechas (`desde`, `hasta`)
- Condicion de enfermedad de base (`conEnfermedadBase`)

## Casos de prueba

### CP-01: Dashboard sin filtros

- Precondicion: existen registros en `diagnostics`.
- Paso: abrir `/dashboard` sin parametros.
- Resultado esperado: se muestran metricas HU9-HU14 y graficas completas.

### CP-02: Filtro por rango de edad para HU4

- Precondicion: existen registros con edades en varios rangos.
- Paso: aplicar `edadMin=18` y `edadMax=35`.
- Resultado esperado: el grafico de valvulopatias por edad concentra registros en `18-35`.

### CP-03: Filtro por genero para HU5

- Precondicion: existen registros con genero `M` y `F`.
- Paso: aplicar `genero=F`.
- Resultado esperado: huellas de HU5 y resto de KPIs muestran solo registros del genero filtrado.

### CP-04: Filtro por enfermedad base para HU6

- Precondicion: existen registros con y sin enfermedades de base.
- Paso: aplicar `conEnfermedadBase=true`.
- Resultado esperado: en HU6 predominan casos `CON ENFERMEDAD DE BASE` y el total coincide con filtro.

### CP-05: Filtros compuestos

- Precondicion: existen datos en multiples instituciones y meses.
- Paso: aplicar `institucionId`, `desde`, `hasta` y `categoriaId`.
- Resultado esperado: todas las graficas se recalculan solo con ese subconjunto.

### CP-06: Busqueda por texto

- Precondicion: hay diagnosticos con texto distintivo en `diagnosticoTexto`.
- Paso: aplicar `q=soplo`.
- Resultado esperado: solo se consideran diagnosticos cuyo texto contiene `soplo` (insensible a mayusculas).

### CP-07: Sin resultados

- Precondicion: filtros imposibles para los datos actuales.
- Paso: aplicar combinacion que no retorna filas.
- Resultado esperado: mensaje de informacion indicando que no hay resultados para filtros y dashboard en cero.

### CP-08: Exportacion HU7 con filtros

- Precondicion: filtros activos en pantalla.
- Paso 1: descargar PDF desde `Descargar PDF`.
- Paso 2: descargar Excel desde `Descargar Excel`.
- Resultado esperado: los archivos contienen solo el subconjunto filtrado (mismos totales que vista).

