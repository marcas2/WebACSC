# Casos de prueba dashboard

## HU-005 Analisis por rango de edad

CA1: Al acceder al modulo, se muestran datos por edad.
CA2: Cuando existan datos, se visualiza la cantidad correctamente.
CA3: Cuando no existen datos, se muestra valor en 0.

Tabla 1. Escenario de prueba HU-005

| Codigo HU | No | Descripcion | Datos | CID |
| --- | --- | --- | --- | --- |
| HU-005 | 1 | Acceso al dashboard y visualizacion de grafica por rango de edad | Usuario autenticado y acceso a /dashboard | 1 |
| HU-005 | 2 | Visualizacion correcta de cantidades por rango de edad con datos existentes | 0-17:1, 18-35:2, 36-59:1, 60+:1 | 2 |
| HU-005 | 3 | Visualizacion en cero cuando no existen registros | Sin registros de diagnosticos | 3 |

Tabla 2. Especificacion caso de prueba HU-005

| CPId | Nombre | Clase | Metodo | HU | Escenario | Valores de entrada | Resultado esperado |
| --- | --- | --- | --- | --- | --- | --- | --- |
| CP00501 | testHU005AccesoModuloEdad | DashboardControllerTest | shouldRenderDashboardWithDataWhenAccessingModule | HU-005 | 1 | hasData=true | Vista dashboard con valvAgeLabels disponible |
| CP00502 | testHU005CantidadEdadCorrecta | DashboardControllerTest | hu005_shouldShowValvulopathiesByAgeRangeCorrectly | HU-005 | 2 | valvAge={0-17:1,18-35:2,36-59:1,60+:1} | valvAgeData=[1,2,1,1] |
| CP00503 | testHU005EdadSinDatosCero | DashboardControllerTest | shouldShowZeroValuesWhenThereIsNoData | HU-005 | 3 | hasData=false | valvAgeData=[0,0,0,0] |

## HU-006 Analisis por genero

CA1: Al acceder al modulo, se muestran datos por genero.
CA2: Cuando existan datos, se visualiza la cantidad correctamente.
CA3: Cuando no existen datos, se muestra valor en 0.

Tabla 3. Escenario de prueba HU-006

| Codigo HU | No | Descripcion | Datos | CID |
| --- | --- | --- | --- | --- |
| HU-006 | 1 | Acceso al dashboard y visualizacion de grafica por genero | Usuario autenticado y acceso a /dashboard | 1 |
| HU-006 | 2 | Visualizacion correcta de cantidades por genero con datos existentes | F:2, M:3 | 2 |
| HU-006 | 3 | Visualizacion en cero cuando no existen registros | Sin registros de diagnosticos | 3 |

Tabla 4. Especificacion caso de prueba HU-006

| CPId | Nombre | Clase | Metodo | HU | Escenario | Valores de entrada | Resultado esperado |
| --- | --- | --- | --- | --- | --- | --- | --- |
| CP00601 | testHU006AccesoModuloGenero | DashboardControllerTest | shouldRenderDashboardWithDataWhenAccessingModule | HU-006 | 1 | hasData=true | Vista dashboard con valvGenderLabels disponible |
| CP00602 | testHU006CantidadGeneroCorrecta | DashboardControllerTest | hu006_shouldShowValvulopathiesByGenderCorrectly | HU-006 | 2 | valvGender={F:2,M:3} | valvGenderData=[2,3] |
| CP00603 | testHU006GeneroSinDatosCero | DashboardControllerTest | shouldShowZeroValuesWhenThereIsNoData | HU-006 | 3 | hasData=false | valvGenderData=[0,0] |

## HU-007 Enfermedades de base

CA1: Al acceder al modulo, se muestran datos por enfermedades de base.
CA2: Cuando existan datos, se visualiza la cantidad correctamente.
CA3: Cuando no existen datos, se muestra valor en 0.

Tabla 5. Escenario de prueba HU-007

| Codigo HU | No | Descripcion | Datos | CID |
| --- | --- | --- | --- | --- |
| HU-007 | 1 | Acceso al dashboard y visualizacion de grafica por enfermedades de base | Usuario autenticado y acceso a /dashboard | 1 |
| HU-007 | 2 | Visualizacion correcta de cantidades por enfermedades de base con datos existentes | CON:4, SIN:1 | 2 |
| HU-007 | 3 | Visualizacion en cero cuando no existen registros | Sin registros de diagnosticos | 3 |

Tabla 6. Especificacion caso de prueba HU-007

| CPId | Nombre | Clase | Metodo | HU | Escenario | Valores de entrada | Resultado esperado |
| --- | --- | --- | --- | --- | --- | --- | --- |
| CP00701 | testHU007AccesoModuloEnfermedadBase | DashboardControllerTest | shouldRenderDashboardWithDataWhenAccessingModule | HU-007 | 1 | hasData=true | Vista dashboard con valvDiseaseLabels disponible |
| CP00702 | testHU007CantidadEnfermedadBaseCorrecta | DashboardControllerTest | hu007_shouldShowUnderlyingDiseasesCorrectly | HU-007 | 2 | valvDisease={CON:4,SIN:1} | valvDiseaseData=[4,1] |
| CP00703 | testHU007SinDatosCero | DashboardControllerTest | shouldShowZeroValuesWhenThereIsNoData | HU-007 | 3 | hasData=false | valvDiseaseData=[0,0] |

## HU-010 Resumen general del sistema

CA1: Al acceder al modulo, se muestra el total de registros.
CA2: Cuando existan datos, se visualiza la cantidad correctamente.
CA3: Cuando no existen datos, se muestra valor en 0.

Tabla 7. Escenario de prueba HU-010

| Codigo HU | No | Descripcion | Datos | CID |
| --- | --- | --- | --- | --- |
| HU-010 | 1 | Acceso al dashboard y visualizacion del total de registros | Usuario autenticado y acceso a /dashboard | 1 |
| HU-010 | 2 | Visualizacion correcta del total de registros con datos existentes | totalRegistros=5 | 2 |
| HU-010 | 3 | Visualizacion en cero cuando no existen registros | Sin registros de diagnosticos | 3 |

Tabla 8. Especificacion caso de prueba HU-010

| CPId | Nombre | Clase | Metodo | HU | Escenario | Valores de entrada | Resultado esperado |
| --- | --- | --- | --- | --- | --- | --- | --- |
| CP01001 | testHU010AccesoModuloTotal | DashboardControllerTest | shouldRenderDashboardWithDataWhenAccessingModule | HU-010 | 1 | hasData=true | Vista dashboard con totalRegistros visible |
| CP01002 | testHU010CantidadTotalCorrecta | DashboardControllerTest | hu010_shouldShowTotalRecordsCorrectly | HU-010 | 2 | totalRegistros=5 | totalRegistros=5 |
| CP01003 | testHU010SinDatosCero | DashboardControllerTest | shouldShowZeroValuesWhenThereIsNoData | HU-010 | 3 | hasData=false | totalRegistros=0 |

## HU-011 Proporcion de sonidos normales y anormales

CA1: Al acceder al modulo, se muestran los datos clasificados.
CA2: Cuando existan datos, se visualiza la cantidad correctamente.
CA3: Cuando no existen datos, se muestra valor en 0.

Tabla 9. Escenario de prueba HU-011

| Codigo HU | No | Descripcion | Datos | CID |
| --- | --- | --- | --- | --- |
| HU-011 | 1 | Acceso al dashboard y visualizacion de normal/anormal | Usuario autenticado y acceso a /dashboard | 1 |
| HU-011 | 2 | Visualizacion correcta de cantidades normal/anormal con datos existentes | NORMAL:3, ANORMAL:2 | 2 |
| HU-011 | 3 | Visualizacion en cero cuando no existen registros | Sin registros de diagnosticos | 3 |

Tabla 10. Especificacion caso de prueba HU-011

| CPId | Nombre | Clase | Metodo | HU | Escenario | Valores de entrada | Resultado esperado |
| --- | --- | --- | --- | --- | --- | --- | --- |
| CP01101 | testHU011AccesoModuloNormalAnormal | DashboardControllerTest | shouldRenderDashboardWithDataWhenAccessingModule | HU-011 | 1 | hasData=true | Vista dashboard con normalStatusLabels disponible |
| CP01102 | testHU011CantidadNormalAnormalCorrecta | DashboardControllerTest | hu011_shouldShowNormalAndAbnormalCountsCorrectly | HU-011 | 2 | normalStatus={NORMAL:3,ANORMAL:2} | normalStatusData=[3,2] |
| CP01103 | testHU011SinDatosCero | DashboardControllerTest | shouldShowZeroValuesWhenThereIsNoData | HU-011 | 3 | hasData=false | normalStatusData=[0,0] |

## HU-012 Registros por tipo de anomalia

CA1: Al acceder al modulo, se muestran los tipos de anomalias.
CA2: Cuando existan datos, se visualiza la cantidad correctamente.
CA3: Cuando no existen datos, se muestra valor en 0.

Tabla 11. Escenario de prueba HU-012

| Codigo HU | No | Descripcion | Datos | CID |
| --- | --- | --- | --- | --- |
| HU-012 | 1 | Acceso al dashboard y visualizacion por tipo de anomalia | Usuario autenticado y acceso a /dashboard | 1 |
| HU-012 | 2 | Visualizacion correcta de cantidades por tipo de anomalia con datos existentes | Estenosis:2, Insuficiencia:3 | 2 |
| HU-012 | 3 | Visualizacion en cero cuando no existen registros | Sin registros de diagnosticos | 3 |

Tabla 12. Especificacion caso de prueba HU-012

| CPId | Nombre | Clase | Metodo | HU | Escenario | Valores de entrada | Resultado esperado |
| --- | --- | --- | --- | --- | --- | --- | --- |
| CP01201 | testHU012AccesoModuloAnomalia | DashboardControllerTest | shouldRenderDashboardWithDataWhenAccessingModule | HU-012 | 1 | hasData=true | Vista dashboard con anomaliaLabels disponible |
| CP01202 | testHU012CantidadAnomaliaCorrecta | DashboardControllerTest | hu012_shouldShowRecordsByAnomalyTypeCorrectly | HU-012 | 2 | categoria={Estenosis:2,Insuficiencia:3} | anomaliaData=[2,3] |
| CP01203 | testHU012SinDatosCero | DashboardControllerTest | shouldShowZeroValuesWhenThereIsNoData | HU-012 | 3 | hasData=false | anomaliaData=[0] |

## HU-013 Registros por foco de auscultacion

CA1: Al acceder al modulo, se muestran los registros por foco.
CA2: Cuando existan datos, se visualiza la cantidad correctamente.
CA3: Cuando no existen datos, se muestra valor en 0.

Tabla 13. Escenario de prueba HU-013

| Codigo HU | No | Descripcion | Datos | CID |
| --- | --- | --- | --- | --- |
| HU-013 | 1 | Acceso al dashboard y visualizacion por foco | Usuario autenticado y acceso a /dashboard | 1 |
| HU-013 | 2 | Visualizacion correcta de cantidades por foco con datos existentes | Aortico:2, Mitral:3 | 2 |
| HU-013 | 3 | Visualizacion en cero cuando no existen registros | Sin registros de diagnosticos | 3 |

Tabla 14. Especificacion caso de prueba HU-013

| CPId | Nombre | Clase | Metodo | HU | Escenario | Valores de entrada | Resultado esperado |
| --- | --- | --- | --- | --- | --- | --- | --- |
| CP01301 | testHU013AccesoModuloFoco | DashboardControllerTest | shouldRenderDashboardWithDataWhenAccessingModule | HU-013 | 1 | hasData=true | Vista dashboard con focoLabels disponible |
| CP01302 | testHU013CantidadFocoCorrecta | DashboardControllerTest | hu013_shouldShowRecordsByAuscultationFocusCorrectly | HU-013 | 2 | foco={Aortico:2,Mitral:3} | focoData=[2,3] |
| CP01303 | testHU013SinDatosCero | DashboardControllerTest | shouldShowZeroValuesWhenThereIsNoData | HU-013 | 3 | hasData=false | focoData=[0] |

## HU-014 Registros por hospital

CA1: Al acceder al modulo, se muestran los registros por hospital.
CA2: Cuando existan datos, se visualiza la cantidad correctamente.
CA3: Cuando no existen datos, se muestra valor en 0.

Tabla 15. Escenario de prueba HU-014

| Codigo HU | No | Descripcion | Datos | CID |
| --- | --- | --- | --- | --- |
| HU-014 | 1 | Acceso al dashboard y visualizacion por hospital | Usuario autenticado y acceso a /dashboard | 1 |
| HU-014 | 2 | Visualizacion correcta de cantidades por hospital con datos existentes | Hospital Norte:2, Hospital Sur:3 | 2 |
| HU-014 | 3 | Visualizacion en cero cuando no existen registros | Sin registros de diagnosticos | 3 |

Tabla 16. Especificacion caso de prueba HU-014

| CPId | Nombre | Clase | Metodo | HU | Escenario | Valores de entrada | Resultado esperado |
| --- | --- | --- | --- | --- | --- | --- | --- |
| CP01401 | testHU014AccesoModuloHospital | DashboardControllerTest | shouldRenderDashboardWithDataWhenAccessingModule | HU-014 | 1 | hasData=true | Vista dashboard con institucionLabels disponible |
| CP01402 | testHU014CantidadHospitalCorrecta | DashboardControllerTest | hu014_shouldShowRecordsByHospitalCorrectly | HU-014 | 2 | institucion={Hospital Norte:2,Hospital Sur:3} | institucionData=[2,3] |
| CP01403 | testHU014SinDatosCero | DashboardControllerTest | shouldShowZeroValuesWhenThereIsNoData | HU-014 | 3 | hasData=false | institucionData=[0] |

## HU-015 Evolucion temporal de registros

CA1: Al acceder al modulo, se muestra la evolucion temporal.
CA2: Cuando existan datos, se visualiza la cantidad correctamente.
CA3: Cuando no existen datos, se muestra valor en 0.

Tabla 17. Escenario de prueba HU-015

| Codigo HU | No | Descripcion | Datos | CID |
| --- | --- | --- | --- | --- |
| HU-015 | 1 | Acceso al dashboard y visualizacion de evolucion temporal | Usuario autenticado y acceso a /dashboard | 1 |
| HU-015 | 2 | Visualizacion correcta de cantidades por periodo con datos existentes | 2026-01:1, 2026-02:2, 2026-03:2 | 2 |
| HU-015 | 3 | Visualizacion en cero cuando no existen registros | Sin registros de diagnosticos | 3 |

Tabla 18. Especificacion caso de prueba HU-015

| CPId | Nombre | Clase | Metodo | HU | Escenario | Valores de entrada | Resultado esperado |
| --- | --- | --- | --- | --- | --- | --- | --- |
| CP01501 | testHU015AccesoModuloEvolucion | DashboardControllerTest | shouldRenderDashboardWithDataWhenAccessingModule | HU-015 | 1 | hasData=true | Vista dashboard con timelineLabels disponible |
| CP01502 | testHU015CantidadEvolucionCorrecta | DashboardControllerTest | hu015_shouldShowTemporalEvolutionCorrectly | HU-015 | 2 | month={2026-01:1,2026-02:2,2026-03:2} | timelineData=[1,2,2] |
| CP01503 | testHU015SinDatosCero | DashboardControllerTest | shouldShowZeroValuesWhenThereIsNoData | HU-015 | 3 | hasData=false | timelineData=[0] |
