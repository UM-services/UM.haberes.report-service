# UM.haberes.report-service

## Estado de Integración Continua

[![UM.haberes.report-service CI](https://github.com/UM-services/UM.haberes.report-service/actions/workflows/maven.yml/badge.svg)](https://github.com/UM-services/UM.haberes.report-service/actions/workflows/maven.yml)

Servicio de generación y gestión de reportes que forma parte de la arquitectura de microservicios de UM. Proporciona funcionalidades para la generación y procesamiento de reportes en diferentes formatos.

## Stack Tecnológico

- Java 25
- Spring Boot 4.1.2
- Spring Cloud 2025.1.4
- Maven 3.8.8+

### Dependencias Principales
- Spring Boot Starter Web
- Spring Cloud Starter Consul Discovery
- Spring Cloud OpenFeign
- Spring Boot Actuator
- Spring Boot Validation
- Spring Boot Mail
- SpringDoc OpenAPI 3.1.1
- OpenPDF 3.0.5
- Apache POI 6.0.0
- Caffeine Cache
- Lombok

## Documentación

- [Documentación del Proyecto](https://um-services.github.io/UM.haberes.report-service)
- [Wiki del Proyecto](https://github.com/UM-services/UM.haberes.report-service/wiki)

## Características Principales

- Generación de reportes en múltiples formatos (PDF, Excel)
- Integración con el sistema de microservicios UM
- Caché implementado con Caffeine
- Monitoreo mediante Spring Actuator
- Registro y descubrimiento de servicios con Consul
- Documentación API con OpenAPI/Swagger
- Envío de correos electrónicos
- Validación de datos con Spring Validation

## Arquitectura

El servicio sigue **arquitectura hexagonal con vertical slicing**. Cada reporte es un slice
independiente bajo `um.haberes.report.reports.<reporte>`:

```
reports/<reporte>/
├── domain/{model, ports/{in,out}}     # sin Spring/OpenPDF/POI
├── application/{usecases, service}    # orquesta puertos
└── infrastructure/{client, render, web/controller, mail}
```

- `shared/` es el kernel: modelos de referencia, puertos de catálogos, clientes Feign y
  `ReportStorage`.
- `infrastructure/render/pdf` concentra `PdfHeaderFooter`, `PdfHeader`, `PdfTables` y
  `PdfMerge`; los renderers de cada slice sólo construyen su layout.
- `shared/infrastructure/web/ReportResponseBuilder` reproduce los headers legacy
  (`attachment` e `inline`).
- Los slices no se importan entre sí y la aplicación no importa infraestructura. Lo verifica
  `HexagonalRulesTest` (`src/test/java/.../contract`).

Los 24 reportes migrados son: `anotador`, `basicosperiodo`, `bono`, `cantidadcargos`,
`cargosclase`, `cargosclaseperiodo`, `cargosperiodo`, `comparacioncodigos`,
`comparacionremuneraciones`, `cursocargonovedad`, `cursodocente`, `cursoscontratado`,
`designacionesdetalle`, `designacionesdocente`, `detallecodigo`, `docentessede`, `fusiondocente`,
`historicoasignacioncargos`, `librosueldos`, `liquidables`, `novedadesdocentes`, `sheet`,
`totalescodigo` y `totalesgenerales`. El detalle de la migración y el mapa bug→corrección están en
[`REFACTOR_HEXAGONAL_PLAN.md`](REFACTOR_HEXAGONAL_PLAN.md) y
[`REFACTOR_HEXAGONAL_NOTES.md`](REFACTOR_HEXAGONAL_NOTES.md).

### Namespace del frontend (`/ui/`) y seguridad

Las pantallas nuevas consumen un namespace propio por reporte, con los parámetros por path:

```
GET /api/haberes/report/ui/{reporte}/data/{params}   -> DTO JSON (tabla)
GET /api/haberes/report/ui/{reporte}/pdf/{params}    -> PDF
GET /api/haberes/report/ui/{reporte}/xlsx/{params}   -> XLSX
```

Los tres formatos salen del mismo `Content` (un `Build<X>ContentUseCase` por slice): la tabla, el
PDF y el XLSX muestran lo mismo. El gateway inyecta `X-API-Key` sólo en
`/api/haberes/report/ui/**` y `ApiKeyFilter` la valida; las rutas legacy que consume `bonos.vb6`
no pasan por el filtro, así que la funcionalidad previa no se altera.

La suite de caracterización (`src/test/java/.../reports`) compara el PDF/XLSX generado
contra un baseline legacy congelado; toda diferencia aprobada se declara en
`src/test/resources/golden/expected-fixes.yml`.

#### Matriz de formatos

La matriz está congelada en `UiFormatMatrixTest` (`contract`): si una ruta se agrega, quita o
renombra sin actualizarla, el build falla.

| Recurso | `data` | PDF | XLSX | Extra |
|---|---|---|---|---|
| `totalesGenerales`, `totalesCodigo`, `comparacionCodigos`, `comparacionRemuneraciones` | ✓ | ✓ | ✓ | |
| `detalleCodigo`, `docentesSede`, `designacionesDocente`, `cursoDocente`, `cursosContratado`, `fusionDocente`, `liquidables`, `cargosPeriodo`, `cursosPeriodo`, `basicosPeriodo`, `cantidadCargos`, `cargosClasePeriodo` | ✓ | ✓ | ✓ | |
| `libroSueldos` | ✓ | ✓ (propio) | ✓ | `POST /lsd/{anho}/{mes}` → ZIP del Libro de Sueldos Digital (lo genera haberes-core) |
| `bonoDependencia` | ✓ | ✓ (consolidado) | — | `sendBono/{legajoId}/{anho}/{mes}` (envío unitario del masivo) |
| `historicoCargos` | ✓ (sólo aviso) | ✓ (sólo encabezado) | — | El cuerpo nunca se implementó en el sistema previo |
| `bono` (individual) | — | ✓ inline | — | `sendBono/{legajoId}/{anho}/{mes}` |

Excepciones justificadas: `bonoDependencia` entrega un PDF consolidado de recibos (el XLSX no
aporta) y `historicoCargos` no tiene cuerpo hasta que exista una spec de producto.

#### Endpoints y consumidores (deuda de retiro)

| Grupo | Cantidad | Consumidor | Estado |
|---|---|---|---|
| Rutas legacy (`/api/haberes/report/<slice>/...`) | 24 | `bonos.vb6` (`Bonos.exe`) | Se mantienen intactas hasta dar de baja el cliente de escritorio |
| Namespace `/ui/**` | 59 | `haberes-frontend` (`feature-consultas`, `feature-bonos`) | Detrás de la API key del gateway |
| Endpoints de lectura agregados en `haberes-core` para reportes (`persona/liquidado`, `novedadacumulado/codigo`, `legajocursoCantidad/periodo`, `cargoliquidacion/periodo` y `/todos`, `cargoclasedetalle/periodo`, `liquidacion-adicional/periodo`, `bono/dependencia/.../print-prepare`) | 8 | Sólo este servicio (Feign) | Internos: no se exponen al navegador |
| `cursocargocontratado/periodo/{anho}/{mes}` (tesoreria-core) | 1 | Sólo este servicio (Feign) | Interno |

- La lógica legacy de planillas (`SheetService`, `ContratadoService`, `LibroSueldoService`) sigue
  en `haberes-core` porque la usa `bonos.vb6`: la duplicación con los slices nuevos es deuda
  deliberada hasta la baja del cliente de escritorio.
- El navegador todavía llama directo a `haberes-core` para catálogos, `bono/{legajoId}/...`
  (`prepare`, `auditoria`, `send-prepare`, `integridad`, `send-prepare`) y `tool/mailvalidate`.
  Ese servicio **no** tiene API key; queda como deuda de seguridad conocida, fuera del alcance de
  este namespace.

## Configuración

El servicio requiere las siguientes configuraciones:

```yaml
spring:
  application:
    name: haberes-report-service
  cloud:
    consul:
      host: consul-service
      port: 8500
      discovery:
        prefer-ip-address: true
        query-passing: true
        health-check-path: /actuator/health/readiness
        health-check-critical-timeout: 5m
        tags: haberes,report
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${app.mail.username}
    password: ${app.mail.password}
    properties:
      mail:
        smtp:
          starttls:
            enable: true
            required: true
          auth: true
          connectiontimeout: 5000
          timeout: 5000
          writetimeout: 5000
management:
  endpoints:
    web:
      exposure:
        include: health
  endpoint:
    health:
      probes:
        enabled: true
      show-details: never
```

## Desarrollo

### Requisitos Previos
- JDK 25
- Maven 3.8.8+
- IDE con soporte para Java/Lombok (IntelliJ IDEA recomendado)

### Construcción
```bash
mvn clean install
```

### Ejecución Local
```bash
mvn spring-boot:run
```

### Docker

#### Construcción Local
```bash
docker build -t um-haberes-report-service .
```

#### Construcción para Producción
```bash
docker build -t um-haberes-report-service .
```

## Licencia

Este proyecto es parte de los servicios internos de la Universidad de Mendoza.

## Estado del Proyecto

Para ver el estado actual del proyecto, issues abiertos y planificación, visita:
- [Issues Activos](https://github.com/UM-services/um.haberes.report-service/issues)
- [Milestones](https://github.com/UM-services/um.haberes.report-service/milestones)
