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

Los 17 reportes migrados son: `anotador`, `bono`, `cargosclase`, `cargosclaseperiodo`,
`comparacioncodigos`, `comparacionremuneraciones`, `cursocargonovedad`, `cursodocente`,
`designacionesdetalle`, `designacionesdocente`, `docentessede`, `fusiondocente`,
`historicoasignacioncargos`, `novedadesdocentes`, `sheet`, `totalescodigo` y
`totalesgenerales`. El detalle de la migración y el mapa bug→corrección están en
[`REFACTOR_HEXAGONAL_PLAN.md`](REFACTOR_HEXAGONAL_PLAN.md) y
[`REFACTOR_HEXAGONAL_NOTES.md`](REFACTOR_HEXAGONAL_NOTES.md).

La suite de caracterización (`src/test/java/.../reports`) compara el PDF/XLSX generado
contra un baseline legacy congelado; toda diferencia aprobada se declara en
`src/test/resources/golden/expected-fixes.yml`.

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
