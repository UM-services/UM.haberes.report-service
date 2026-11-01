# Notas del refactor hexagonal

Mapa entre cada corrección aprobada (`REFACTOR_HEXAGONAL_PLAN.md` §2) y el lugar donde
quedó implementada. Todos los reportes viven ahora en `reports/<reporte>/` con la misma
separación `domain → application → infrastructure`; el kernel compartido está en `shared/`
y los componentes de render/web comunes en `infrastructure/`.

## Correcciones

| ID | Corrección | Dónde quedó |
|---|---|---|
| C1 | Reporte sin datos ⇒ PDF válido con encabezado + `"Sin datos para el período."`; siempre se cierra el `Document` | Renderers de todos los slices (`reports/*/infrastructure/render`) |
| C2 | NPE por datos opcionales/nulos | Ensambladores y casos de uso de cada slice; `BonoContentAssembler`, `GenerateCursocargoNovedad…`, `GenerateDesignacionesDetalle…`, etc. |
| C3 | `Collectors.toMap` con merge `(a,b)->a` | Casos de uso de los 17 slices |
| C4 | Orden determinista de sedes | `GenerateCargosClasePeriodoReportUseCaseImpl` (`Map.Entry.comparingByKey`) |
| C5 | Sin estado mutable compartido | `GenerateDocentesSedeReportUseCaseImpl` recibe todo por parámetro; no hay campos de instancia mutables |
| C6 | `number_2_text`: cero, negativos, miles de millones, decimales | `shared/domain/model/NumerosEnLetras` + `NumerosEnLetrasTest` |
| C7 | Mail sin direcciones ⇒ `"ERROR: Sin correos para ENVIAR"`; `mailInstitucional` null-safe | `SendBonoUseCaseImpl` + `SmtpBonoMailAdapter` |
| C8 | Nombre interno de `docentessede` sin repetir `facultadId` | `GenerateDocentesSedeReportUseCaseImpl.fileName` |
| C9 | Auditoría sólo si el PDF se generó; timestamp con `Clock` truncado a ms | `GenerateBonoReportUseCaseImpl` (audita después del render) |
| C10 | Código muerto e inyecciones sin uso | Se eliminaron `Tool`, `Jsonifier`, `Jsonifyable` y los servicios/controladores legacy |
| C11 | NPE de `HeaderFooter.onStartPage` | `PdfHeaderFooter.onStartPage` |
| C12 | Cabecera de cargos duplicada en el recibo/detalle | `BonoPdfRendererImpl` y `DetalleCargosPdfRendererImpl` (una sola celda de encabezado) |
| C13 | Título "Rechazados" sólo si hay rechazados | `AnotadorPdfRendererImpl` |
| C14 | No imprimir el literal `null` | `GenerateAnotadorReportUseCaseImpl` (`Objects.toString(value, "")`) |
| C15 | `created` como `dd/MM/yyyy HH:mm` separado del usuario | `GenerateAnotadorReportUseCaseImpl` + `AnotadorPdfRendererImpl` |
| C16 | Nombres internos distintos para pendientes y A/R | `GenerateAnotadorReportUseCaseImpl.fileName` |
| C17 | Typo "Asignaión" → "Asignación" | `HistoricoAsignacionCargosPdfRendererImpl` |
| C18 | Subtotal de `TotalesGenerales` sin etiqueta | **Sin cambio** (posible intención) |
| C19 | `DocentesSede`: facultad/sede nulas ⇒ `ReportException` antes de abrir el PDF | `GenerateDocentesSedeReportUseCaseImpl` |
| C20 | `ComparacionRemuneraciones`: no enmascarar errores runtime como 200 con PDF vacío | `ComparacionRemuneracionesPdfRendererImpl` propaga `ReportException` |

## Diferencias declaradas contra el baseline legacy

Todas están en `src/test/resources/golden/expected-fixes.yml`, con el ID de corrección y el
escenario. Cualquier otra diferencia hace fallar `ReportParityTest`/los tests de reporte.

## Estructura

```
um.haberes.report
├── reports/<reporte>/
│   ├── domain/{model, ports/{in,out}}
│   ├── application/{usecases, service}
│   └── infrastructure/{client, render, web/controller, mail}
├── shared/
│   ├── domain/{model, ports/out}
│   ├── application/exception
│   └── infrastructure/{client, file, web}
└── infrastructure/render/pdf   (PdfHeaderFooter, PdfHeader, PdfTables, PdfMerge)
```

`HexagonalRulesTest` verifica automáticamente que no haya imports entre slices, que el
dominio no dependa de frameworks y que la aplicación no importe infraestructura, OpenPDF,
POI ni Jakarta Mail. También prohíbe `@RequestParam`, `@Query`, `@ModelAttribute` y los
paquetes legacy.
