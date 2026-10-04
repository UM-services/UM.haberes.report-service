# UM.haberes.report-service

## Estado de Integración Continua

[![UM.haberes.report-service CI](https://github.com/UM-services/UM.haberes.report-service/actions/workflows/maven.yml/badge.svg)](https://github.com/UM-services/UM.haberes.report-service/actions/workflows/maven.yml)

Servicio de generación y gestión de reportes que forma parte de la arquitectura de microservicios de UM. Proporciona funcionalidades para la generación y procesamiento de reportes en diferentes formatos.

## Stack Tecnológico

- Java 25
- Spring Boot 4.1.1
- Spring Cloud 2025.1.3
- Maven 3.8.8+

### Dependencias Principales
- Spring Boot Starter Web
- Spring Cloud Starter Consul Discovery
- Spring Cloud OpenFeign
- Spring Boot Actuator
- Spring Boot Validation
- Spring Boot Mail
- SpringDoc OpenAPI 3.1.0
- OpenPDF 3.0.5
- Apache POI 5.5.1
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
    consul:
      host: consul-service
      port: 8500
      discovery:
        prefer-ip-address: true
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
