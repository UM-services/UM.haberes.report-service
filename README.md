# UM.haberes.report-service

## Estado de Integración Continua

[![UM.haberes.report-service CI](https://github.com/UM-services/UM.haberes.report-service/actions/workflows/maven.yml/badge.svg)](https://github.com/UM-services/UM.haberes.report-service/actions/workflows/maven.yml)

Servicio de generación y gestión de reportes que forma parte de la arquitectura de microservicios de UM. Proporciona funcionalidades para la generación y procesamiento de reportes en diferentes formatos.

## Stack Tecnológico

- Java 21
- Kotlin 2.1.20
- Spring Boot 3.4.4
- Spring Cloud 2024.0.1
- Maven 3.8.8+

### Dependencias Principales
- Spring Boot Starter Web
- Spring Cloud Netflix Eureka Client
- Spring Cloud OpenFeign
- Spring Boot Actuator
- Spring Boot Validation
- Spring Boot Mail
- SpringDoc OpenAPI 2.8.6
- OpenPDF 2.0.3
- Apache POI 5.4.0
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
- Registro y descubrimiento de servicios con Eureka
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

eureka:
  instance:
    prefer-ip-address: true
  client:
    fetch-registry: true
    register-with-eureka: true
    service-url:
      defaultZone: http://eureka:@eureka-service:8761/eureka
```

## Desarrollo

### Requisitos Previos
- JDK 21
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
