# C4 de la arquitectura del dashboard

Este documento describe la arquitectura con la notacion C4, usando tres niveles: contexto, contenedores y componentes. Esta version es especifica para la aplicacion Spring Boot del proyecto WebACSC, incluyendo el flujo del dashboard y las piezas de autenticacion y persistencia.

## 1. System Context

```plantuml
@startuml
!include C4_Context.puml

title WebACSC - System Context

Person(usuario, "Usuario autenticado", "Accede al dashboard y realiza operaciones desde la web o la API")

System(webacsc, "WebACSC", "Aplicacion web Spring Boot para gestion clinica y dashboard estadistico")

Rel(usuario, webacsc, "Usa")

@enduml
```

## 2. Container

```plantuml
@startuml
!include C4_Container.puml

title WebACSC - Container Diagram

Person(usuario, "Usuario autenticado", "Usa la aplicacion desde el navegador")

System_Boundary(webacsc, "WebACSC") {
    Container(web, "Web application", "Spring Boot + Thymeleaf + Spring MVC", "Renderiza vistas, expone APIs REST, ejecuta casos de uso y servicios de aplicacion")
    ContainerDb(db, "Relational Database", "PostgreSQL", "Persistencia de usuarios, diagnosticos, catalogos y relaciones")
}

Rel(usuario, web, "HTTP/HTTPS")
Rel(web, db, "JPA / SQL")

@enduml
```

## 3. Component - Dashboard

```plantuml
@startuml
!include C4_Component.puml

title WebACSC - Component Diagram: Dashboard

Container_Boundary(web, "Web application") {
    Component(dashboardController, "DashboardController", "Spring MVC controller", "Expone /dashboard y /dashboard/export")
    Component(dashboardService, "DashboardDiagnosticService", "Application service", "Calcula indicadores, valida datos y prepara agregaciones para el dashboard")
    Component(excelExport, "ExcelExportUtil", "Utility", "Genera exportacion en XLSX")
    Component(pdfExport, "PdfExportUtil", "Utility", "Genera exportacion en PDF")
    Component(diagnosticJpa, "DiagnosticJpaRepository", "Spring Data JPA repository", "Consulta agregaciones y conteos del dominio Diagnostic")
}

Rel(dashboardController, dashboardService, "Solicita metricas")
Rel(dashboardController, excelExport, "Exporta en Excel")
Rel(dashboardController, pdfExport, "Exporta en PDF")
Rel(dashboardService, diagnosticJpa, "Lee agregaciones")

@enduml
```

## 4. Component - Use cases y persistencia

```plantuml
@startuml
!include C4_Component.puml

title WebACSC - Component Diagram: Application and Persistence

Container_Boundary(web, "Web application") {
    Component(authController, "AuthController / AuthApiController", "Spring MVC / REST controller", "Entrada de autenticacion y registro")
    Component(userController, "UserManagementController", "Spring MVC controller", "Administracion de usuarios")
    Component(diagnosticController, "DiagnosticApiController", "REST controller", "Creacion y consulta de diagnosticos")

    Component(loginUseCase, "LoginUseCase", "Use case", "Autentica credenciales")
    Component(registerUseCase, "RegisterUseCase", "Use case", "Registra usuarios")
    Component(createUserUseCase, "CreateUserUseCase", "Use case", "Crea usuarios")
    Component(updateUserUseCase, "UpdateUserUseCase", "Use case", "Actualiza usuarios")
    Component(updatePasswordUseCase, "UpdateUserPasswordUseCase", "Use case", "Actualiza contrasena")
    Component(deleteUserUseCase, "DeleteUserUseCase", "Use case", "Elimina usuarios")
    Component(getAllUsersUseCase, "GetAllUsersUseCase", "Use case", "Lista usuarios")
    Component(createDiagnosticUseCase, "CreateDiagnosticUseCase", "Use case", "Crea diagnosticos y relaciones")

    Component(authDomainService, "AuthDomainService", "Domain service", "Reglas de autenticacion y seguridad de negocio")

    Component(userRepository, "UserRepository", "Domain repository", "Puerto de usuarios")
    Component(roleRepository, "RoleRepository", "Domain repository", "Puerto de roles")
    Component(diagnosticRepository, "DiagnosticRepository", "Domain repository", "Puerto de diagnosticos")
    Component(focoRepository, "FocoRepository", "Domain repository", "Puerto de foco")
    Component(categoriaRepository, "CategoriaAnomaliaRepository", "Domain repository", "Puerto de categoria de anomalia")
    Component(enfermedadRepository, "EnfermedadBaseRepository", "Domain repository", "Puerto de enfermedades base")

    Component(userRepoImpl, "UserRepositoryImpl", "Persistence adapter", "Implementa UserRepository con JPA")
    Component(roleRepoImpl, "RoleRepositoryImpl", "Persistence adapter", "Implementa RoleRepository con JPA")
    Component(diagnosticRepoAdapter, "DiagnosticRepositoryAdapter", "Persistence adapter", "Implementa DiagnosticRepository con JPA")
    Component(focoRepoImpl, "FocoRepositoryImpl", "Persistence adapter", "Implementa FocoRepository con JPA")
    Component(categoriaRepoImpl, "CategoriaAnomaliaRepositoryImpl", "Persistence adapter", "Implementa CategoriaAnomaliaRepository con JPA")
    Component(enfermedadRepoImpl, "EnfermedadBaseRepositoryImpl", "Persistence adapter", "Implementa EnfermedadBaseRepository con JPA")

    Component(userJpa, "UserJpaRepository", "Spring Data JPA", "Acceso a tabla de usuarios")
    Component(roleJpa, "RoleJpaRepository", "Spring Data JPA", "Acceso a tabla de roles")
    Component(diagnosticJpa, "DiagnosticJpaRepository", "Spring Data JPA", "Acceso a tabla de diagnosticos")
    Component(focoJpa, "FocoJpaRepository", "Spring Data JPA", "Acceso a tabla de foco")
    Component(categoriaJpa, "CategoriaAnomaliaJpaRepository", "Spring Data JPA", "Acceso a tabla de categoria de anomalia")
    Component(enfermedadJpa, "EnfermedadBaseJpaRepository", "Spring Data JPA", "Acceso a tabla de enfermedades base")
}

Rel(authController, loginUseCase, "Usa")
Rel(authController, registerUseCase, "Usa")
Rel(userController, createUserUseCase, "Usa")
Rel(userController, updateUserUseCase, "Usa")
Rel(userController, updatePasswordUseCase, "Usa")
Rel(userController, deleteUserUseCase, "Usa")
Rel(userController, getAllUsersUseCase, "Usa")
Rel(diagnosticController, createDiagnosticUseCase, "Usa")

Rel(loginUseCase, authDomainService, "Aplica reglas")
Rel(registerUseCase, authDomainService, "Aplica reglas")
Rel(createUserUseCase, userRepository, "Persiste usuarios")
Rel(updateUserUseCase, userRepository, "Actualiza usuarios")
Rel(updatePasswordUseCase, userRepository, "Actualiza contrasena")
Rel(deleteUserUseCase, userRepository, "Elimina usuarios")
Rel(getAllUsersUseCase, userRepository, "Lista usuarios")
Rel(createDiagnosticUseCase, diagnosticRepository, "Guarda diagnostico")
Rel(createDiagnosticUseCase, focoRepository, "Resuelve foco")
Rel(createDiagnosticUseCase, categoriaRepository, "Resuelve categoria")
Rel(createDiagnosticUseCase, enfermedadRepository, "Resuelve enfermedades base")

Rel(userRepository, userRepoImpl, "Implementado por")
Rel(roleRepository, roleRepoImpl, "Implementado por")
Rel(diagnosticRepository, diagnosticRepoAdapter, "Implementado por")
Rel(focoRepository, focoRepoImpl, "Implementado por")
Rel(categoriaRepository, categoriaRepoImpl, "Implementado por")
Rel(enfermedadRepository, enfermedadRepoImpl, "Implementado por")

Rel(userRepoImpl, userJpa, "Usa")
Rel(roleRepoImpl, roleJpa, "Usa")
Rel(diagnosticRepoAdapter, diagnosticJpa, "Usa")
Rel(focoRepoImpl, focoJpa, "Usa")
Rel(categoriaRepoImpl, categoriaJpa, "Usa")
Rel(enfermedadRepoImpl, enfermedadJpa, "Usa")

@enduml
```

## Lectura rapida

El nivel de contexto muestra a la persona que usa la aplicacion y el sistema completo.

El nivel de contenedores separa la aplicacion Spring Boot y la base de datos relacional.

El nivel de componentes descompone el monolito en controladores, casos de uso, servicios de dominio, puertos de repositorio y adaptadores JPA, con un foco especial en el dashboard y en el flujo de autenticacion y administracion.

Si quieres, el siguiente paso puede ser reducirlo a un unico diagrama C4 por nivel para pegarlo en README, o generar una version visual en PlantUML lista para exportar a PNG/SVG.