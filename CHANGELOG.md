# Changelog

Todos los cambios notables en este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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

## [Unreleased]

### Added
- Integración con Spring Boot Validation para validación de datos
- Soporte para generación de reportes en formato PDF usando OpenPDF
- Soporte para generación de reportes en formato Excel usando Apache POI
- Integración con Spring Cloud Netflix Eureka para registro de servicios
- Implementación de caché usando Caffeine
- Documentación API con OpenAPI/Swagger
- Soporte para envío de correos electrónicos
- Configuración de Docker para desarrollo y producción

### Changed
- Actualización de Spring Boot a versión 3.4.4
- Actualización de Spring Cloud a versión 2024.0.1
- Actualización de Kotlin a versión 2.1.20
- Actualización de SpringDoc OpenAPI a versión 2.8.6

### Dependencies
- Spring Boot Starter Web
- Spring Cloud Netflix Eureka Client
- Spring Cloud OpenFeign
- Spring Boot Actuator
- Spring Boot Validation
- Spring Boot Mail
- SpringDoc OpenAPI
- OpenPDF 2.0.3
- Apache POI 5.4.0
- Caffeine Cache
- Lombok

## [0.0.1-SNAPSHOT] - 2024-03-XX

### Added
- Configuración inicial del proyecto
- Estructura básica del servicio
- Integración con el sistema de microservicios UM 