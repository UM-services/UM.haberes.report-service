# UM.haberes.report-service

## Estado de Integración Continua

[![UM.haberes.report-service CI](https://github.com/UM-services/UM.haberes.report-service/actions/workflows/maven.yml/badge.svg)](https://github.com/UM-services/UM.haberes.report-service/actions/workflows/maven.yml)

Servicio de generación y gestión de reportes que forma parte de la arquitectura de microservicios de UM. Proporciona funcionalidades para la generación y procesamiento de reportes en diferentes formatos.

## Stack Tecnológico

- Java 24
- Kotlin 2.2.20
- Spring Boot 3.5.6
- Spring Cloud 2025.0.0
- Maven 3.8.8+

### Dependencias Principales
- Spring Boot Starter Web
- Spring Cloud Starter Consul Discovery
- Spring Cloud OpenFeign
- Spring Boot Actuator
- Spring Boot Validation
- Spring Boot Mail
- SpringDoc OpenAPI 2.8.10
- OpenPDF 3.0.0
- Apache POI 5.4.1
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

## Configuración

El servicio requiere las siguientes configuraciones:

```yaml
spring:
  application:
    name: haberes-report-service
  cloud:
    config:
      enabled: true
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

spring:
  cloud:
    consul:
      host: localhost
      port: 8500
      discovery:
        register: true
        instance-id: ${spring.application.name}:${random.value}
```

## Desarrollo

### Requisitos Previos
- JDK 24
- Maven 3.8.8+
- IDE con soporte para Kotlin (IntelliJ IDEA recomendado)

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
docker build -f Dockerfile.local -t um-haberes-report-service .
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
