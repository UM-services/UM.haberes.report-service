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

## [0.3.0] - 2026-04-04

### Changed
- Actualización de Spring Boot a versión 4.0.5
- Actualización de Kotlin a versión 2.3.20
- Actualización de OpenPDF a versión 3.0.3
- Actualización de SpringDoc OpenAPI a versión 3.0.2

### Added
- Nueva dependencia Commons FileUpload para gestión de archivos

### Infrastructure
- Actualización de GitHub Actions a versiones más recientes
- Actualización de JDK a 25 en workflow de documentación

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

## [0.0.1-SNAPSHOT] - 2024-03-XX

### Added
- Configuración inicial del proyecto
- Estructura básica del servicio
- Integración con el sistema de microservicios UM 