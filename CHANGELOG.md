# Changelog

Todos los cambios notables en este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.8.1] - 2026-10-29

### Fixed
- Se elimina una línea sobrante con un `_|` residual del banner ASCII de arranque
  (`src/main/resources/banner.txt`), que además queda con salto de línea final, para que el
  banner del servicio se imprima sin caracteres sueltos.

## [0.8.0] - 2026-10-25

### Added
- **F4 — especiales del menú Consultas.**
  - `bonosDependencia` (C1): grilla de legajos de una dependencia (`FormularioImprimirClient`,
    una sola llamada a `formularioImprimir`), PDF consolidado de los recibos y envío unitario
    para el masivo. Endpoints `GET /ui/bonoDependencia/{data|pdf}/{anho}/{mes}/{dependenciaId}/{salida}`
    y `GET /ui/bonoDependencia/sendBono/{legajoId}/{anho}/{mes}`. Las rutas `/ui/` no auditan ni
    reciben IP: la preparación de actividad y la auditoría las resuelve el core en
    `POST /bono/dependencia/.../print-prepare`.
  - `historicoCargos` (C18): `BuildHistoricoAsignacionCargosContentUseCase` y
    `GET /ui/historicoCargos/{data|pdf}/{categoriaId}`. `data` devuelve el aviso de que el
    reporte no tiene cuerpo implementado; no hay XLSX. Se dejaron de consultar los cuatro
    catálogos que el reporte pedía y no usaba (el PDF es idéntico).
- **Namespace del bono individual unificado**: `/api/haberes/report/bono/ui/{generatePdf,sendBono}`
  pasó a `/api/haberes/report/ui/bono/...` (`BonoUiController`), de modo que queda detrás de
  `ApiKeyFilter`. Las rutas legacy con `{ipAddress}` siguen en `BonoController` sin cambios para
  `bonos.vb6`.
- Namespace nuevo para el frontend bajo `/api/haberes/report/ui/{reporte}/{data|pdf|xlsx}/...`,
  con parámetros por path. Las rutas legacy que consume `bonos.vb6` quedan intactas.
- `totalesGenerales`: `BuildTotalesGeneralesContentUseCase` (contenido compartido), DTO
  `TotalesGeneralesResponse` + mapper, export XLSX (`TotalesGeneralesXlsxRendererImpl`) y los
  endpoints `/ui/totalesGenerales/data|pdf|xlsx/{anho}/{mes}`. La tabla, el PDF y el XLSX salen
  del mismo contenido.
- `totalesCodigo`, `comparacionCodigos` y `comparacionRemuneraciones`: mismo patrón (builder +
  `Response` + mapper + renderer XLSX) y endpoints `/ui/*/{data,pdf,xlsx}/...`. En comparación de
  remuneraciones los parámetros incluyen el período anterior.
- `designacionesdocente`, `cursodocente`, `fusiondocente`, `docentessede` y
  `cargosclaseperiodo`: mismo patrón (contenido compartido + `Response` + mapper + XLSX) y
  endpoints `/ui/*/{data,pdf,xlsx}/...`.
- Eficiencia con core: `cursodocente` y `fusiondocente` consultan el período completo en lugar de
  legajo por legajo (nuevo `GET /cursofusion/periodo/{anho}/{mes}` en core);
  `cargosclaseperiodo` usa `cargoclaseperiodo/facultad/{id}` y `cargoclasedetalle/facultad/...` en
  lugar de iterar por legajo, y memoiza la definición por cargoClaseId.
- Caché Caffeine de catálogos casi estáticos (facultades, geográficas, dependencias, categorías,
  clases, cargos, cursos y códigos) con TTL 30 min, desactivada en los tests
  (`spring.cache.type=none`).
- `infrastructure/render/xlsx/XlsxCells`: helpers compartidos para escribir celdas con estilo en
  los XLSX nuevos.
- Planillas de F3 (un contenido por reporte, con `data` + PDF + XLSX): `liquidables`,
  `cantidadcargos`, `detallecodigo`, `cargosperiodo`, `basicosperiodo`, `librosueldos` y
  `cursoscontratado` (7 slices nuevos), más la slice `sheet` extendida para `cursosPeriodo`
  (contenido extraído a `BuildSheetCursosContentUseCase`, PDF nuevo y `SheetCursosUiController`;
  el XLSX y la ruta legacy `/sheet/generatecursos` quedan iguales).
  - `liquidables`: sin filtro de período (sale del flag vigente) y `Sin datos.` como leyenda;
    `/ui/liquidables/{data,pdf,xlsx}`.
  - `cantidadcargos`: vista `vw_legajo_curso_cantidad` + bruto del código 96;
    `/ui/cantidadCargos/{data,pdf,xlsx}/{anho}/{mes}`.
  - `detallecodigo`: importe liquidado y acumulado de novedades del código, con el código en el
    título; `/ui/detalleCodigo/{data,pdf,xlsx}/{codigoId}/{anho}/{mes}`.
  - `cargosperiodo`: cargos activos del período (sólo lectura, **no** replica el borrado de
    cargos no liquidados del sistema previo); `/ui/cargosPeriodo/...`.
  - `cursosperiodo`: `/ui/cursosPeriodo/{data,pdf,xlsx}/{anho}/{mes}`.
  - `librosueldos`: tabla, PDF propio y XLSX (sin el PDF legal del Crystal, no migrado) más
    `POST /ui/libroSueldos/lsd/{anho}/{mes}` que devuelve el ZIP del Libro de Sueldos Digital de
    haberes-core para los legajos visibles.
  - `basicosperiodo`: mezcla cargos, cargos con clase y adicionales del período;
    `/ui/basicosPeriodo/...`.
  - `cursoscontratado`: cursos de contratados del período (una consulta a tesoreria-core en lugar
    del N+1 por contratado); `/ui/cursosContratado/...`.
- `shared/domain/model/Importes`: formato único de importes de las planillas (dos decimales
  fijos) y `ReportFile.Format.ZIP` para el archivo del LSD.
- Puertos y adaptadores nuevos del kernel: `ItemCatalogPort`, `NovedadAcumuladoCatalogPort`,
  `LegajoCursoCantidadCatalogPort`, `CargoClaseDetalleCatalogPort`,
  `LiquidacionAdicionalCatalogPort`, `ContratadoPersonaCatalogPort`,
  `CursoCargoContratadoCatalogPort` y `LibroSueldosZipPort`, más las consultas por período de
  `CargoLiquidacionCatalogPort` y `LiquidacionCatalogPort`.

### Fixed
- Los errores de negocio de los reportes se devuelven como **400** con `ProblemDetail` en lugar
  de 500 genérico (`ReportBusinessException` + `ReportExceptionHandler`), de modo que el frontend
  muestre el mensaje real (p. ej. «No hay liquidaciones para la dependencia…»). Además, un 404
  del servicio de origen (facultad/sede inexistente) se traduce a **404** y el resto de las
  fallas de Feign a **502**, en vez de 500 sin detalle.

### Security
- `ApiKeyFilter`: la API key que inyecta el gateway se exige **sólo** en
  `/api/haberes/report/ui/**`; actuator, springdoc y todo el namespace legacy quedan fuera del
  filtro (mismo esquema que `compras-service`, sin romper la funcionalidad previa).
- `app.api-key` en `bootstrap.yml` (`APP_API_KEY`, default `default-secret-key`).

### Tests
- `TotalesGeneralesUiTest` (JSON + PDF + XLSX con goldens nuevos `*-xlsx.txt`),
  `ApiKeyFilterTest` (401 en `/ui/**`, 200 con clave y 200 sin clave en ruta legacy) y
  `BuildTotalesGeneralesContentUseCaseTest`.
- `TotalesCodigoUiTest`, `ComparacionCodigosUiTest` y `ComparacionRemuneracionesUiTest` con
  goldens XLSX por reporte; `BuildComparacionRemuneracionesContentUseCaseTest` (el caso de uso de
  PDF ahora sólo renderiza).
- `DesignacionesDocenteUiTest`, `DocentesSedeUiTest`, `CursoDocenteUiTest`, `FusionDocenteUiTest`
  y `CargosClasePeriodoUiTest` con goldens XLSX; `BuildCargosClasePeriodoContentUseCaseTest` y el
  caso de uso de PDF reducido a render.
- `HexagonalRulesTest`: la regla "una slice no importa controller/DTO de otra" ahora compara
  slices; antes también marcaba los DTOs propios como violación.
- Planillas de F3: `LiquidablesUiTest`, `CantidadCargosUiTest`, `DetalleCodigoUiTest`,
  `CargosPeriodoUiTest`, `SheetCursosUiTest`, `LibroSueldosUiTest` (incluye el ZIP del LSD por
  `POST`), `BasicosPeriodoUiTest` y `CursosContratadoUiTest`, con goldens nuevos
  (`normal.txt`, `normal-xlsx.txt` y los escenarios vacíos). El XLSX de `sheet` conserva su
  golden (`sheet/normal.txt`) y el PDF nuevo usa `cursosperiodo/normal.txt`. Además, los 24
  endpoints nuevos quedaron en `endpoints.txt` y `coverage.txt`.
- F4: `BonosDependenciaUiTest` (grilla, PDF consolidado sin auditar y envío sin auditar) e
  `HistoricoAsignacionCargosUiTest` (aviso + PDF reutilizando el golden legacy).
  `BonoReportTest` actualizado a las rutas `/ui/bono/*` con API key. Los stubs del fixture
  `bono` se movieron a `support/BonoStubs` para compartirlos entre las dos suites.
- F5: `ApiKeyFilterTest` suma un caso parametrizado por endpoint (recorre `endpoints.txt` y exige
  401 sin clave en **todas** las rutas `/ui/**`, incluida la única `POST`) y `UiFormatMatrixTest`
  congela la matriz de formatos por reporte con sus excepciones (C1 y C18 sin XLSX, LSD por POST).
  Los errores de negocio se verifican con 400/404 en `BonosDependenciaUiTest` y `DocentesSedeUiTest`.

### Documentation
- `README.md` documenta el namespace `/ui/**` (los tres formatos salen del mismo `Content`), la
  matriz de formatos congelada por `UiFormatMatrixTest` y el inventario de endpoints y
  consumidores, incluida la deuda de retiro de las rutas legacy.
- `docs/diagrams/arquitectura-general.mmd` incorpora el gateway que inyecta `X-API-Key` y
  `docs/diagrams/flujo-reportes.mmd` refleja los formatos `data`/PDF/XLSX del namespace nuevo.

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
