# Changelog

Todos los cambios notables en este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.7.0] - 2026-10-24

### Changed
- Refactor hexagonal completo del servicio: los 17 reportes viven en
  `um.haberes.report.reports.<reporte>` con `domain/application/infrastructure`, sin
  servicios ni controladores legacy. El detalle está en `REFACTOR_HEXAGONAL_PLAN.md` y
  `REFACTOR_HEXAGONAL_NOTES.md`.
- `shared/` concentra los modelos de referencia, puertos de catálogos y clientes Feign; los
  componentes comunes de PDF quedaron en `infrastructure/render/pdf`.
- `NumerosEnLetras` reemplaza a `Tool.number_2_text` e incorpora cero, negativos y miles de
  millones (C6).
- Se eliminaron `Tool`, `Jsonifier`, `Jsonifyable` y los paquetes legacy de nivel superior
  (`service`, `controller`, `model`, `client` y `util`).

### Fixed
- Se corrigieron C1–C17 y C19–C20 del inventario aprobado (C18 queda documentado sin cambio):
  cierre de documentos y PDF válido sin datos, null-safety, `toMap` con merge, orden
  determinista de sedes, concurrencia en `docentessede`, nombres de archivo internos,
  auditoría condicionada, cabecera duplicada del bono, formato/estado del anotador y
  propagación de errores en `comparacionremuneraciones`.
- Cada diferencia contra el baseline legacy está declarada en
  `src/test/resources/golden/expected-fixes.yml`.

### Tests
- Suite de caracterización de los 26 endpoints (contract tests + goldens PDF/XLSX).
- `HexagonalRulesTest` valida reglas de arquitectura escaneando las fuentes.
- `NumerosEnLetrasTest` congela la tabla de conversión de importes a letras.
- Nueva dependencia de test `spring-boot-starter-webmvc-test` (las anotaciones web de
  MockMvc viven en un módulo propio en Spring Boot 4).

### Documentation
- `README.md` incorpora la sección de arquitectura hexagonal con vertical slicing y el
  inventario de los 17 slices.
- `docs/diagrams/arquitectura-general.mmd` y `docs/diagrams/flujo-reportes.mmd` se
  reescribieron para describir las capas `domain`, `application` e `infrastructure`, los
  puertos y el flujo caso de uso → adaptador → render, en lugar del controlador-servicio
  monolítico anterior.
- Se agregan `REFACTOR_HEXAGONAL_PLAN.md` y `REFACTOR_HEXAGONAL_NOTES.md` con el plan, las
  decisiones y el mapa bug→corrección.

## [0.6.2] - 2026-10-23

### Changed
- Actualización de Spring Boot a versión 4.1.2
- Actualización de Spring Cloud a versión 2025.1.4
- Actualización de Lombok a versión 1.18.48
- Actualización de Apache POI (`poi` y `poi-ooxml`) a versión 6.0.0
- Actualización de SpringDoc OpenAPI a versión 3.1.1
- Actualización de `commons-lang3` a versión 3.21.0
- Actualización de los plugins `maven-compiler-plugin` (3.16.0) y `jacoco-maven-plugin` (0.8.15)
- El discovery de Consul resuelve únicamente instancias con sus checks en `passing` (`query-passing: true`), evitando que Feign/LoadBalancer enruten hacia instancias críticas
- Se exponen explícitamente los grupos de probes de Spring Boot (`management.endpoint.health.probes.enabled`) y el endpoint `health` (`management.endpoints.web.exposure.include`), con `show-details: never`, para que Consul consulte `/actuator/health/readiness`

### Tests
- Nuevo `ConsulReadinessConfigTest`, que congela el contrato de readiness declarado en `bootstrap.yml` (`health-check-path`, `health-check-critical-timeout`, `query-passing`, exposición de probes y `show-details`)

## [0.6.1] - 2026-10-21

### Changed
- El chequeo de salud que registra el servicio en Consul ahora consulta `/actuator/health/readiness` (`health-check-path`), el grupo de probes de disponibilidad de Spring Boot, que sólo refleja el estado de readiness de la aplicación y no el de dependencias externas
- Consul poda la instancia del catálogo si el chequeo permanece crítico durante `5m` (`health-check-critical-timeout`)

### Documentation
- El generador de documentación (`.github/workflows/generate-docs.yml`) escapa el contenido de los diagramas Mermaid antes de insertarlo con `innerHTML`, evitando que anotaciones con `<<`/`>>` corrompan el render publicado en GitHub Pages

## [0.6.0] - 2026-10-18

### Added
- Nuevo entorno de despliegue `staging-new` para el servicio de reportes de haberes

### Changed
- El job `deploy` de `deploy-staging.yml` ahora usa una matriz de GitHub Actions (`fail-fast: false`) sobre los entornos `[staging, staging-new]`; cada ejecución usa el runner y el GitHub Environment del entorno correspondiente (`runs-on: [self-hosted, linux, "<entorno>"]`, `environment.name: ${{ matrix.environment }}`)
- El nombre del job de despliegue de staging se parametriza por entorno: `Deploy staging service (<entorno>)`

### Documentation
- Corregido en `README.md` el comando de construcción local de Docker, que referenciaba un `Dockerfile.local` inexistente en el repositorio

## [0.5.0] - 2026-09-20

### Added
- Nuevos endpoints exclusivos del frontend en `BonoController`: `GET /ui/generatePdf/{legajoId}/{anho}/{mes}` y `GET /ui/sendBono/{legajoId}/{anho}/{mes}`, que generan/envían el bono sin registrar auditoría (la IP real la resuelve el core server-side); el contrato legacy VB6 con `{ipAddress}` en el path se mantiene intacto
- Nuevos workflows de GitHub Actions `deploy-develop.yml` y `deploy-staging.yml` para verificación, build y publicación por rama

### Changed
- Migración completa de Kotlin a Java: los DTOs de `um.haberes.report.kotlin.dto.*` (haberes y tesoreria) se reescribieron como clases Java + Lombok bajo `um.haberes.report.model.*`, sin cambios en el contrato JSON expuesto
- Eliminadas la dependencia Kotlin, `jackson-module-kotlin` y el `kotlin-maven-plugin` del build Maven
- Actualización de Spring Boot a versión 4.1.1
- Actualización de Spring Cloud a versión 2025.1.3
- Actualización de SpringDoc OpenAPI a versión 3.1.0
- Constructores explícitos de `AnotadorController` y `CargosClaseController` reemplazados por `@RequiredArgsConstructor` de Lombok
- `e.printStackTrace()` en `BonoService` reemplazado por logging con `log.error`
- Actualización del banner de arranque (`banner.txt`)

### Fixed
- `sendBono` ahora valida que el nombre del PDF generado no sea nulo o vacío antes de enviar el correo, evitando errores al procesar el adjunto

## [0.4.0] - 2026-07-10

### Added
- Nueva interfaz `Jsonifyable` con método `jsonify()` para serialización JSON simplificada de DTOs
- Implementación de `Jsonifyable` en DTOs `FacultadDto` y `GeograficaDto`

### Changed
- Simplificado logging de DTOs en `DocentesSedeService` usando `jsonify()` en lugar de `JsonMapper` directo

### Fixed
- Corrección de potencial `NullPointerException` al acceder a `designacionTipo.getNombre()` en `DocentesSedeService`

## [0.3.0] - 2026-06-20

### Changed
- Actualización de Spring Boot a versión 4.1.0
- Actualización de Kotlin a versión 2.4.0
- Actualización de Spring Cloud a versión 2025.1.3
- Actualización de OpenPDF a versión 3.0.5
- Actualización de SpringDoc OpenAPI a versión 3.0.3
- Cambio de formato de fecha ISO 8601 en DTOs: `Z` → `XX` para compatibilidad con Java 25

### Added
- Nueva dependencia Commons FileUpload para gestión de archivos

### Infrastructure
- Actualización de GitHub Actions a versiones más recientes
- Actualización de JDK a 25 en workflow de documentación

### Documentation
- Corrección de nombre de proyecto en pipeline de documentación
- Adición de diagramas Mermaid faltantes (arquitectura, flujo, ERD, despliegue)

## [0.2.0] - 2026-02-02

### Changed
- Actualización de Spring Boot a versión 4.0.2
- Actualización de Java a versión 25
- Actualización de Kotlin a versión 2.3.0
- Actualización de Spring Cloud a versión 2025.1.0
- Actualización de Apache POI a versión 5.5.1
- Actualización de SpringDoc OpenAPI a versión 3.0.1
- Actualización de Commons Lang3 a versión 3.20.0
- Actualización de JDK en GitHub Actions workflow a 25
- Actualización de Dockerfile para usar JDK 25

## [0.1.0] - 2025-10-09

### Added
- Nuevo controlador y servicio para docente designación
- Nuevo controlador y servicio para cargos de clase
- Nuevo controlador y servicio para cursos por docente
- Clase utilitaria Jsonifier para serialización JSON mejorada
- Migración de registro de servicios de Eureka a Consul

### Changed
- Actualización de Spring Boot a versión 3.5.6
- Actualización de Kotlin a versión 2.2.20
- Actualización de OpenPDF a versión 3.0.0
- Actualización de SpringDoc OpenAPI a versión 2.8.10
- Migración de biblioteca PDF de lowagie a openpdf
- Refactorización de controladores y servicios para usar @RequiredArgsConstructor de Lombok
- Mejora en el logging de errores y depuración
- Cambio de firma en bonos de sueldo

### Fixed
- Corrección en manejo de excepciones en generación de PDFs

## [0.0.1-SNAPSHOT] - 2024-03-XX

### Added
- Configuración inicial del proyecto
- Estructura básica del servicio
- Integración con el sistema de microservicios UM
