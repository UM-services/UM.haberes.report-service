# Plan de refactor hexagonal — `haberes-report` (UM.haberes.report-service)

> Estado: **completado — F0, F1, F2, F3 (5 olas), F4 y F5**. Los 17 reportes están migrados
> a slices hexagonales y no queda código legacy. Base: `develop` @ `13926a0`.
> **Sin rama nueva y sin commits** (D7). Decisiones tomadas: **D1 = 17 slices**,
> **D2 = modelos purpose-built**, **D3 = corregir todos los bugs (C1–C10 aprobados)**,
> **D4 = fixtures a mano**, **D7 = seguir en `develop`**.
>
> Aprobado además: reporte sin datos ⇒ PDF válido con encabezado + `"Sin datos para el período."`;
> `historico` queda sin cambio funcional (sólo encabezado, documentado); **no** se optimiza el N+1.

---

## 0. Reconocimiento (verificado)

| Dato | Valor |
|---|---|
| Endpoints HTTP | **26** (17 controllers, todos `@GetMapping`) |
| Clientes Feign | **39** |
| DTOs de wire | **42** |
| Services | **18** (~7.900 LOC; `BonoService` = 1.391 LOC) |
| LOC main | 10.086 |
| Tests existentes | 2 (`contextLoads`, `ConsulReadinessConfigTest`) |
| Persistencia | ninguna (no JPA, no `@Query`, no `@RequestParam`) |
| Duplicación | **14 copias** de `HeaderFooter` (~100 LOC c/u) |
| `mvn -o test` hoy | verde |

Consumidores a no romper: `bonos.vb6` (`clsCtlPrint.cls`, `clsCtlSheet.cls`, `frmImprimir.frm`)
y `haberes-frontend` (specs con las URLs exactas).

**POC de tests ya validada y descartada:** `@SpringBootTest` + `@MockitoBean` sobre Feign,
`path.files=target/…`, imágenes desde el basedir y `new PdfTextExtractor(reader).getTextFromPage(n)`.
Generó PDF y extrajo texto de forma determinista (`Tests run: 1, Failures: 0`).

---

## 1. Diagnóstico

1. Controller → service gordo: fetch remoto + reglas + layout + disco + mail + auditoría.
2. `Environment` (`path.files`) en los 18 services; OpenPDF/POI dentro de `service/`;
   `JavaMailSender` en `BonoService`.
3. HTTP fuera de `web/`: `Tool.generateFile*` y headers armados a mano en `SheetController`.
4. DTOs de wire usados como dominio (`PersonaDto` con Jackson).
5. `BonoService`: 1.391 LOC, 22 colaboradores, 6 puntos de entrada, 2 overrides públicos.
6. Efectos colaterales escondidos en la generación (delete/upserts/mail/auditoría).
7. Fallos silenciosos + `Document` sin cerrar (ver §2, bugs C1/C2).
8. 14 `HeaderFooter` duplicados, nombres de archivo repetidos, `number_2_text` en un `util`.
9. Sin puertos, casos de uso ni `ReportException`.
10. `DocentesSedeService` guarda `facultad`/`geografica` en **campos de instancia de un singleton**
    (condición de carrera real).

---

## 2. Inventario de bugs y corrección propuesta (D3 = corregir todo)

Cada corrección queda identificada con un ID. En F0 se captura el **baseline legacy** de todos
los escenarios y se guarda; el gate compara el resultado nuevo contra ese baseline y exige que
**la diferencia sea exactamente el conjunto de IDs aprobados** (§4.3).

### C1 — Ciclo de vida del `Document` / archivo

| Evidencia | Propuesta |
|---|---|
| `return` tempranos con `document.open()` sin `close()`: `ComparacionCodigos` (3), `TotalesCodigo` (3), `ComparacionRemuneraciones` (4), `CargosClasePeriodo` (3), `FusionDocente` (4), `Historico` (4), `CursoDocente` (3), `DesignacionesDocente` (3), `CursocargoNovedad` (catch), `DesignacionesDetalle` (catch), `Bono` (catch) | **Siempre** cerrar (try/finally o try-with-resources). Si no hay datos, generar un **PDF válido** con encabezado + línea `"Sin datos para el período."` y log `warn`. |
| Errores de IO capturados que devuelven el nombre de un archivo inexistente/parcial | El renderer propaga; el caso de uso registra `log.error` y `ReportStorage.load` lanza `ReportException("No se pudo generar el reporte <X>")`. HTTP sigue siendo **500**. |
| `BonoService.mergePdf` no cierra `PdfReader`/`OutputStream` ante excepción | try-with-resources. |
| `generatePdfDependencia` sin liquidaciones devuelve `""` y luego 500 sin contexto | `ReportException("No hay liquidaciones para la dependencia <id> en <m>/<a>")` → 500. |

### C2 — NPE por datos opcionales / nulos

| Evidencia | Propuesta |
|---|---|
`TotalesGenerales`: `totalMensualClient.findAllByPeriodo(...)` null→NPE; `totalMensual.getTotal()` null→NPE | listas null = vacías; total faltante = celda vacía (no rompe el reporte).
`TotalesCodigo`/`ComparacionCodigos`: `getTotal()` null → `compareTo` NPE | total null = `BigDecimal.ZERO`.
`BonoService`: `items.get(1)` ausente → `new ItemDto()` con `importe` null → NPE en `getImporte().add(...)` | `ItemDto` de dominio con `importe = ZERO` por defecto; básico 0 si no hay ítem 1.
`BonoService`: `antiguedad` null, `control` null, `persona` null, fechas `control` null, `indices` vacío (`getFirst()`) | `ReportException` con mensaje claro (son datos **requeridos** para el recibo). `indices` vacío → `0` (antigüedad ETEC no aplicable) en lugar de `NoSuchElementException`.
`BonoService`: `legajoBanco` null → fallback a `new LegajoBancoDto()` (ya existe) | mantener; CBU vacío si null.
`Anotador*`: cliente devuelve null → NPE en for-each; `persona`/`facultad` null → NPE | lista null = vacía; persona/facultad null = `"Sin datos"` en esa fila.
`CursoDocente`/`DesignacionesDocente`: `assert` (no-op con assertions off) y luego NPE al buscar facultad/geográfica | referencia faltante = fila omitida + `warn` (no NPE).
`SheetService`: `curso`/`facultad`/`geografica`/`cargoTipo` faltante → NPE | celda vacía + `warn`; la fila se emite igual.
`NovedadesDocentes`: `novedad.getCurso()` null → NPE; cliente null → NPE | null-safe; sin cursos = reporte con encabezado y "Sin datos".
`CargosClasePeriodo`: `facultad` null → NPE en header | encabezado sin facultad + `warn`.
`FusionDocente`: `personaClient.findAll()` se llama **dos veces**; `toMap` por legajo sin merge | una sola llamada; merge `(a,b)->a`.
`Mail`: `contacto.getMailInstitucional().isEmpty()` → NPE si es null | null-safe → `"ERROR: Sin correos para ENVIAR"`.

### C3 — `Collectors.toMap` sin merge (posible `IllegalStateException: Duplicate key`)

`BonoService` (dependencias), `TotalesCodigo` (2), `ComparacionCodigos` (2), `TotalesGenerales`,
`Historico` (4), `CargosClasePeriodo` (2), `CursoDocente` (4), `DesignacionesDocente` (2),
`FusionDocente` (4), `SheetService` (4), `ComparacionRemuneraciones` (1).
**Propuesta:** merge `(a, b) -> a` en todos (primer registro gana), igual que el patrón ya usado
en `ComparacionRemuneraciones`/`CursoDocente`.

### C4 — Orden no determinista

`CargosClasePeriodo.detallesPorSede` es `HashMap` → orden de sedes arbitrario.
**Propuesta:** `LinkedHashMap` + orden alfabético por nombre de sede (determinista y legible).
`DesignacionesDetalle.mapa` es `HashMap` pero es solo lookup → sin cambio.
`TotalesGenerales.codigosPorGrupo` se recorre por `ordenFijoGrupos` → sin cambio.

### C5 — Estado mutable compartido (concurrencia)

`DocentesSedeService.facultad`/`geografica` son campos del singleton.
**Propuesta:** pasarlos como parámetros (el renderer recibe un `DocentesSedeContent` inmutable).

### C6 — `number_2_text`

- `0` → `""` (falta "cero").
- Negativos y ≥ 1.000.000.000 → `switch` sin rama → `""`.
**Propuesta:** `0 → "cero"`, negativos → `"menos " + …`, extender a miles de millones,
`ReportException` si excede el rango soportado. Se congela con tabla de tests
(1, 15, 21, 100, 101, 999, 1.000, 1.001, 21.000, 1.000.000, 2.000.000, 0, negativos, decimales).

### C7 — Mail

`addresses` vacío ⇒ `helper.setTo(new String[0])` revienta; `mailInstitucional` null ⇒ NPE.
**Propuesta:** si no hay direcciones → devolver el literal `"ERROR: Sin correos para ENVIAR"`
(mismo mensaje que ya existe para "sin contacto"). El resto de los literales y el orden
(generar → mail → `legajoControl.bonoEnviado = 1`) se preserva.

### C8 — Nombres de archivo

`DocentesSedeService`: `docentes.{facultadId}.{geograficaId}.{anho}.{mes}.{facultadId}.pdf`
(repite `facultadId`). **Propuesta:** `docentes.{facultadId}.{geograficaId}.{anho}.{mes}.pdf`
(archivo interno; el nombre HTTP `docentesSede.pdf` no cambia).

### C9 — Auditoría del bono

Hoy se audita **aunque la generación haya fallado**. **Propuesta:** auditar sólo si el PDF se
generó correctamente; el timestamp pasa a `OffsetDateTime.now(Clock)` truncado a milisegundos
(mismo instante UTC), con `Clock` inyectable para tests.

### C10 — Código muerto / inyecciones sin uso

`Tool.dateAbsoluteArgentina()` (sin uso), `CargoClient` inyectado y nunca usado en
`HistoricoAsignacionCargosService`, `claseMap` sin uso en `CargosClasePeriodoService`,
bloque comentado en `HistoricoAsignacionCargosService`.
**Propuesta:** eliminar. El reporte `historico` queda **sin cambio funcional** (sólo encabezado),
documentado como funcionalidad incompleta preexistente (ver §2.1).

Además: `HeaderFooter.onStartPage` se ejecuta antes de `setHeader`, con `headerTable` nulo, y
captura/loguea un `NullPointerException` en **todos** los reportes (ruido, no rompe el PDF).
Se elimina el NPE (`headerTable == null → noop`).

### 2.1. Correcciones adicionales aprobadas en G0 (C11–C20)

Detectadas al congelar el baseline de F0. Aprobadas el 2026-10-24, **excepto C18**.

| ID | Corrección |
|---|---|
| C11 | Eliminar el NPE de `HeaderFooter.onStartPage` (header aún nulo en la primera página). |
| C12 | Bono: agregar una sola vez la celda de encabezado de cargos (hoy la columna "Dep Cargo Básico" se duplica). |
| C13 | Anotador A/R: imprimir el título "Rechazados" sólo si hay rechazados. |
| C14 | Anotador: no imprimir el literal `null` cuando `respuesta`/`anotacion` son nulos. |
| C15 | Anotador: formatear `created` como `dd/MM/yyyy HH:mm` y separarlo del usuario. |
| C16 | Nombres internos distintos para pendientes y autorizados/rechazados por facultad (hoy colisionan y dos requests concurrentes se pisan). |
| C17 | Histórico: corregir el typo "Asignaión" → "Asignación". |
| C19 | `DocentesSedeService`: mover `jsonify()` dentro del `try` y propagar `ReportException` si falta facultad/sede. |
| C20 | `ComparacionRemuneracionesService`: acotar el `catch` a IO/Document y propagar `ReportException` (hoy un error runtime deja PDF de 0 bytes con 200). |

**C18 queda documentado y sin cambio**: la celda de subtotal de `TotalesGenerales` no tiene
etiqueta; puede ser intencional.

### 2.2. Fuera de alcance (requiere spec de producto, no es bug)

1. **`Historico de Asignación de Cargos`**: el cuerpo del loop está vacío y la consulta real está
   comentada; hoy genera sólo el encabezado. Restaurarlo **no** es un refactor: hay que definir
   qué liquidaciones se listan. Propuesto: dejarlo igual y documentarlo.
2. **N+1 de llamadas** (`CursoDocente`, `CargosClasePeriodo`, `SheetService`, `FusionDocente`):
   correctitud intacta; cambiarlo altera el patrón de llamadas al core. Propuesto: no tocarlo.
3. **`itemClient.deleteAllByZero` dentro de un GET** y upsert de `letra`: efectos intencionales
   del recibo. Se preservan y se documentan.
4. **Etiqueta de subtotal vacía** en `TotalesGenerales` (variable `subtotalLabelCell` sin
   `Phrase`): puede ser intencional. Propuesto: no cambiar.

---

## 3. Arquitectura objetivo (17 slices + kernel autorizado en D1)

```
um.haberes.report
├── ReportApplication.java
├── configuration/ReportConfiguration.java       Feign + @PropertySource + Clock
│
├── shared/                                      ← KERNEL (fuera de los slices)
│   ├── domain/model/                            resúmenes de referencia + ReportFile
│   ├── domain/ports/out/                        puertos usados por ≥2 slices
│   └── infrastructure/client/{haberes,tesoreria}/
│       ├── dto/                                 DTO de wire (Jackson/Feign) — 42
│       ├── client/                              39 @FeignClient (mismos paths)
│       ├── mapper/                              wire ↔ resumen de dominio
│       └── adapter/                             implementa los puertos del kernel
│
├── infrastructure/
│   ├── file/ReportStorageAdapter.java           path.files
│   ├── mail/SmtpBonoMailAdapter.java
│   ├── render/shared/{PdfHeaderFooter,PdfTables,PdfMerge}.java
│   └── web/shared/{ReportResponseBuilder,ReportExceptionHandler}.java
│
└── reports/
    ├── bono/{domain/{model,ports/{in,out}}, application/{exception,service,usecases}, infrastructure/{client,render,web,mail}}
    ├── anotador/…
    ├── cargosclase/…
    ├── cargosclaseperiodo/…
    ├── comparacioncodigos/…
    ├── comparacionremuneraciones/…
    ├── cursocargonovedad/…
    ├── cursodocente/…
    ├── designacionesdetalle/…
    ├── designacionesdocente/…
    ├── docentessede/…
    ├── fusiondocente/…
    ├── historicoasignacioncargos/…
    ├── novedadesdocentes/…
    ├── sheet/…
    ├── totalescodigo/…
    └── totalesgenerales/…
```

**Reglas que se verifican automáticamente (§4.6):**
- `reports.X` **no** importa `reports.Y` (ni dominio, ni app, ni infra).
- `shared` no importa `reports.*`.
- `domain/**` (de cada slice y del kernel) sólo `java.*` + `lombok.*`.
- `application/**` no importa `infrastructure.*`, Spring Cloud, OpenPDF, POI, Jakarta Mail.
- `infrastructure/web/**` no importa `infrastructure/client/**` ni `dto/**`.
- Sin `@RequestParam`, `@Query`, `@ModelAttribute`; sin paquetes legacy.

### 3.1. Contenido del kernel (purpose-built, D2)

Sólo lo que usan **2 o más** slices, con los campos que esos slices necesitan:

`PersonaResumen` (legajoId, documento, apellido, nombre, cuil, dependenciaId, altaAdministrativa,
directivoEtec), `FacultadResumen` (facultadId, nombre), `GeograficaResumen` (geograficaId, nombre),
`DependenciaResumen` (dependenciaId, nombre, acronimo), `CategoriaResumen` (categoriaId, nombre,
basico), `CargoTipoResumen` (cargoTipoId, nombre), `ClaseResumen` (claseId, nombre, valorHora),
`CodigoResumen` (codigoId, nombre), `CursoResumen` (cursoId, nombre, facultadId, geograficaId,
anual, semestre1, semestre2), `CursoCargoResumen`, `CursoCargoNovedadResumen`,
`LiquidacionResumen`, `TotalResumen`, `ReportFile`.

Cada slice define en **su** `domain/model` sólo lo específico (ej.: `BonoRecibo`, `BonoConcepto`,
`AnotadorFila`, `DesignacionesDetalleDatos`, `CursoDocenteFila`).
Cada slice declara en **su** `domain/ports/out` los datos que sólo él usa (ej.: bono → `ControlPort`,
`ItemPort`, `LetraPort`, `LegajoBancoPort`, `LegajoControlPort`, `BonoImpresionPort`, `ContactoPort`).
El kernel expone los catálogos compartidos (`PersonaCatalogPort`, `CursoCatalogPort`, …).

### 3.2. Respuesta HTTP

`ReportResponseBuilder` (infra/web compartida) reproduce **exactamente**:
- attachment: `Content-Disposition: attachment; filename=<X>`, `Cache-Control`, `Pragma`,
  `Expires`, `Content-Length`, `application/octet-stream`.
- inline: `Content-Disposition: inline; filename="<X>"`, `application/pdf`.

No se crea DTO de response binario: el controller devuelve `ResponseEntity<Resource>` y no expone
tipos de dominio ni de wire (desviación justificada y documentada).

---

## 4. Plan de tests (escritos y verdes **antes** de tocar producción)

### 4.1. Contrato HTTP (26 tests)
`@SpringBootTest` + `@AutoConfigureMockMvc` (se agrega `spring-boot-starter-webmvc-test` test-scope,
ya presente en `~/.m2`), `@MockitoBean` de los 39 Feign + `JavaMailSender`, Consul apagado.
Verifica por endpoint: status, `Content-Type`, `Content-Disposition`, cache headers,
`Content-Length`, firma binaria (`%PDF-` / `PK\x03\x04`), 400 por tipo inválido, 404 de ruta.
El 500 se verifica con `webEnvironment = RANDOM_PORT`.

### 4.2. Interacciones y efectos colaterales
Bono: auditoría sí/no, `deleteAllByZero` siempre, upsert de letra, merge de dependencia,
mail + `legajoControl`, literales de retorno, IP y `Clock` fijo.
DesignacionesDetalle: tolerancia por colección y fallbacks.

### 4.3. Baseline legacy + goldens con diff aprobado (clave para D3)

1. **F0** genera con el código **actual** un baseline por escenario y lo guarda en
   `src/test/resources/legacy-baseline/` (texto PDF / dump XLSX).
2. Tras el refactor, `ReportParityTest` compara salida nueva vs baseline y exige que el diff
   (línea a línea, normalizado) sea **exactamente** el declarado en
   `src/test/resources/golden/expected-fixes.yml`:
   ```yaml
   - id: C1-empty-totales-codigo
     report: totales-codigo
     scenario: vacio
     reason: "El PDF ahora se cierra y agrega 'Sin datos para el período.'"
   ```
3. Cualquier diferencia no declarada ⇒ test rojo. Esto demuestra simultáneamente "no se perdió
   nada" y "sólo cambió lo que decidimos cambiar".
4. Se conservan además goldens "post-fix" por escenario para futuras regresiones.

Escenarios por reporte: `normal`, `vacio`, `nulos`; más `especiales` (tildes, comillas, montos
0/negativos/redondeo) y `multipagina` (60+ filas) en los 8 con tablas/paginación.

### 4.4. Lógica pura
`NumerosEnLetrasTest`, `NombreArchivoTest`, `EtiquetasTest` (estado/tipo/dictado) y tests de
filtros/orden/totales por slice.

### 4.5. Adaptadores y casos de uso
Un test por adapter (argumentos exactos al Feign, incluido `novedadClient.findByUnique(..., "null")`).
Un test por caso de uso con puertos mockeados (selección, orden, totales, errores C1/C2).

### 4.6. Reglas de arquitectura
`HexagonalRulesTest` con escaneo de fuentes (sin dependencias nuevas): todas las reglas de §3.

### 4.7. Determinismo
Fixtures JSON fijos, `Clock` fijo, salida a `target/test-output/…`, sin red ni Consul,
imágenes verificadas (`marca_um.png`, `firma_new.png`), comparación de texto extraído (no bytes).

---

## 5. Fases y puertas

### F0 — Caracterización + baseline (no toca `main`)
`pom.xml` (+1 dep test), perfil de test, fixtures, 26 contract tests, baseline legacy,
tests de interacción y lógica pura. `NumerosEnLetras` se testea contra el `Tool` actual.
- **G0:** `mvn -o verify` verde; 26/26 endpoints y 17/17 reportes con baseline congelado.

### F1 — Kernel
Resúmenes de referencia, puertos compartidos, 42 wire DTO + 39 Feign movidos a
`shared/infrastructure/client`, mappers wire↔resumen, adapters, `ReportStorageAdapter`,
`SmtpBonoMailAdapter`, `Clock`, `ReportException`.
Tests: mappers (incluye nulls y campos obligatorios) y adapters.
- **G1:** suite verde (nada cableado a los endpoints todavía).

### F2 — Render compartido
`PdfHeaderFooter` parametrizado (reemplaza las 14 copias preservando cada variante),
`PdfTables`, `PdfMerge`.
- **G2:** paridad de encabezados/pies contra baseline.

### F3 — Olas de slices (switch atómico por reporte)

| Ola | Slices | Bugs principales |
|---|---|---|
| 1 | cargosclase, cargosclaseperiodo, comparacioncodigos, comparacionremuneraciones, totalescodigo, totalesgenerales | C1, C2, C3, C4 |
| 2 | cursodocente, designacionesdocente, docentessede, fusiondocente, novedadesdocentes, historicoasignacioncargos | C1, C2, C3, C5, C8, C10 |
| 3 | cursocargonovedad, designacionesdetalle, anotador (4 endpoints) | C1, C2 |
| 4 | sheet | C1, C2, C3 |
| 5 | bono (6 endpoints) | C1, C2, C3, C6, C7, C9 |

Cada ola: puertos+use case+renderer+controller nuevos, **borrado** del controller/service viejos,
suite completa + `ReportParityTest`.
- **G3.1 … G3.5:** suite verde por ola; ningún diff fuera de `expected-fixes.yml`.

### F4 — Limpieza
Borrar `service/`, `controller/`, `model/`, `client/`, `util/`; habilitar `HexagonalRulesTest`.
- **G4:** `mvn -o verify` verde con reglas activas.

### F5 — Documentación
README (Arquitectura), `docs/diagrams/arquitectura-general.mmd` y `flujo-reportes.mmd`,
CHANGELOG `Unreleased`, `REFACTOR_HEXAGONAL_NOTES.md` con el mapa bug→corrección.
Sin commit/tag/push/version bump sin autorización.

---

## 6. Riesgos

| Riesgo | Mitigación |
|---|---|
| Drift no intencional | baseline legacy + `expected-fixes.yml` obligatorio |
| Pérdida de campos | modelos purpose-built + tests de adapter/caso de uso por campo usado |
| Cambios de orden/format | escenarios `multipagina` y `especiales`; diff explícito |
| 17 slices → duplicación | kernel con resúmenes compartidos; arch test impide imports cruzados |
| Doble mapping durante migración | switch atómico por slice |
| Concurrencia (C5) | contenido inmutable pasado por parámetro + test paralelo del slice |
| Consul/red | perfil test sin Consul; `mvn -o` |
| Bugs "de producto" (§2.1) | quedan explícitamente fuera y documentados |

---

## 7. Entregables y esfuerzo

| Fase | Entregable | Volumen aprox. |
|---|---|---|
| F0 | contract tests + baseline + interacción + lógica pura | ~2.500 LOC test, ~80 fixtures/goldens |
| F1 | kernel + client + storage + mail | ~3.500 LOC |
| F2 | render compartido | ~700 LOC |
| F3 | 17 slices / 26 endpoints | ~7.000 LOC |
| F4 | limpieza + reglas | −3.000 LOC legacy |
| F5 | docs | README + 2 diagramas + CHANGELOG |

Cada puerta (G0…G4) permite parar y revisar. **G0 es la más importante**: allí ya está
demostrado que el comportamiento actual quedó congelado y que los cambios posteriores son
exactamente los aprobados en §2.

---

## 11. Estado de ejecución

### F0 — COMPLETADA (G0 cumplida)

| Elemento | Estado |
|---|---|
| `pom.xml` + `spring-boot-starter-webmvc-test` (test) | hecho |
| `src/test/resources/application-test.yml` (Consul off, `path.files=target/test-output/`) | hecho |
| `support/AbstractReportIntegrationTest` (39 Feign + `JavaMailSender` mockeados, MockMvc) | hecho |
| `support/GoldenFiles` (PDF texto + XLSX celdas, baseline legacy + golden + diff) | hecho |
| `support/Fixtures` (sobre JSON por reporte/escenario) | hecho |
| `support/ReportHttp` (status, content-type, disposition, cache, length, firma) | hecho |
| `contract/EndpointContractManifestTest` + `endpoints.txt` | verde (26/26) |
| `contract/EndpointCoverageTest` + `coverage.txt` | verde (cada endpoint → test existente y `@Test`) |
| 17 slices caracterizados | **51 tests, 0 fallos** (`mvn -o clean test`) |
| Baseline legacy + golden | 40 + 40 archivos |

Cobertura por reporte: anotador 8, bono 7, cargosclase 2, cargosclaseperiodo 2,
comparacioncodigos 2, comparacionremuneraciones 2, cursocargonovedad 4, cursodocente 2,
designacionesdetalle 2, designacionesdocente 2, docentessede 2, fusiondocente 2,
historico 2, novedadesdocentes 2, sheet 2, totalescodigo 2, totalesgenerales 2.

**Evidencia de "no se perdió nada":** los 40 goldens contienen datos reales (recibo del bono con
nombre/CUIL/conceptos/neto, Excel con 3 filas y tipos de celda, designaciones con totales,
anotador con estados, etc.) y están congelados contra el baseline legacy. Cualquier diferencia
futura hace fallar el test.

**Contratos preservados:** no se modificó `src/main/**` ni los endpoints, paths, nombres de
archivo o headers. `git diff -- src/main` vacío; el único cambio de build es la dependencia
`spring-boot-starter-webmvc-test` con scope `test`.

### Hallazgos nuevos de F0 — clasificados en G0

Aprobados: **C11–C17, C19 y C20** (ver §2.1). **C18** queda documentado sin cambio.

| ID | Hallazgo | Propuesta |
|---|---|---|
| **C11** | `HeaderFooter.onStartPage` loguea NPE en todos los reportes (`headerTable` null) | eliminar el NPE (ya aprobado en C10) |
| **C12** | `BonoService`: la misma `PdfPCell` se agrega dos veces a `tableGrupo` → columna "Dep Cargo Básico" duplicada en el recibo | agregar una sola vez (cambio visual) |
| **C13** | Anotador A/R imprime el título "Rechazados" aunque la lista esté vacía | imprimirlo sólo si hay rechazados |
| **C14** | Anotador imprime `"null"` cuando `respuesta`/`anotacion` son null | imprimir vacío |
| **C15** | Anotador imprime `created` con `LocalDateTime.toString()` (`2026-09-12T08:30`) pegado al usuario | `dd/MM/yyyy HH:mm` con separación |
| **C16** | Nombre interno `anotador.<facultad>.<anho>.<mes>.pdf` compartido por pendientes y A/R por facultad → dos requests concurrentes se pisan | nombres internos distintos |
| **C17** | `Historico` imprime el typo "Asignaión" | "Asignación" |
| **C18** | `TotalesGenerales`: celda de subtotal sin etiqueta (`subtotalLabelCell` sin `Phrase`) | dejar como está (posible intent) |
| **C19** | `DocentesSedeService.generate`: `facultad.jsonify()`/`geografica.jsonify()` fuera del `try` → 500 si el core devuelve null | mover dentro del try + `ReportException` |
| **C20** | `ComparacionRemuneracionesService`: `catch (Exception)` enmascara errores runtime y deja PDF de 0 bytes con 200 | acotar el catch a IO/Document y propagar `ReportException` |

### Estado de archivos de test

```
src/test/java/um/haberes/report/
├── contract/{EndpointContractManifestTest,EndpointCoverageTest}
├── reports/{17 paquetes}/…ReportTest.java
└── support/{AbstractReportIntegrationTest,Fixtures,GoldenFiles,ReportHttp}

src/test/resources/
├── application-test.yml
├── contract/{endpoints.txt,coverage.txt}
├── fixtures/{17 reportes}/{normal|vacio}.json
├── golden/{17 reportes}/…txt        (40)
└── legacy-baseline/{17 reportes}/…txt (40)
```


### F1 — COMPLETADA (G1 cumplida)

**F1a — movimiento al kernel (hecho).**
- 81 archivos movidos: 42 wire DTO (`model/{haberes,tesoreria}/core`) y 39 `@FeignClient`
  (`client/{haberes,tesoreria}/core`) a
  `shared/infrastructure/client/{haberes,tesoreria}/core/{dto,client}`.
- 74 archivos con imports reescritos; `@EnableFeignClients(basePackages = "…shared.infrastructure.client")`.
- Verificación de que el movimiento fue **100% mecánico**: los 81 archivos comparados contra el
  índice de git, normalizando solo los 4 prefijos de paquete → **0 diferencias**. Ningún path,
  verbo, nombre o header de endpoint cambió.

**F1b — kernel de catálogos (hecho).**
- `shared/domain/model`: `PersonaResumen`, `FacultadResumen`, `GeograficaResumen`,
  `DependenciaResumen`, `CategoriaResumen`, `CargoTipoResumen`, `ClaseResumen`, `CodigoResumen`,
  `ReportFile` (record + `Format`).
- `shared/domain/ports/out`: `PersonaCatalogPort`, `FacultadCatalogPort`, `GeograficaCatalogPort`,
  `DependenciaCatalogPort`, `CategoriaCatalogPort`, `CargoTipoCatalogPort`, `ClaseCatalogPort`,
  `CodigoCatalogPort`, `ReportStorage`.
- `shared/infrastructure/client/haberes/core/{mapper,adapter}`: 8 mappers wire→dominio y 8
  adapters Feign (listas null → vacías, objeto null → `Optional.empty`).
- `shared/infrastructure/file/ReportStorageAdapter` (lee `path.files`, valida existencia y tamaño).
- `shared/application/exception/ReportException`; bean `Clock` en `ReportConfiguration`.
- Tests: `shared/CatalogAdapterTest` (6 tests) — mapeo, nulls, delegación exacta y storage.
- Suite completa: **57 tests, 0 fallos**.

**F1c — agregados académicos/transaccionales (hecho).**
- Modelos: `CursoResumen`, `CursoCargoResumen`, `LiquidacionResumen`, `CargoLiquidacionResumen`,
  `CursoFusionResumen`, `CursoCargoNovedadResumen`, `TotalNovedadResumen`, `TotalItemResumen`,
  `DocenteDesignacionResumen`, `DesignacionTipoResumen`.
- Puertos: `CursoCatalogPort`, `CursoCargoCatalogPort`, `LiquidacionCatalogPort`,
  `CargoLiquidacionCatalogPort`, `CursoFusionCatalogPort`, `CursoCargoNovedadCatalogPort`,
  `TotalesPeriodoPort`, `DocenteDesignacionCatalogPort`.
- Mappers anidados (curso→facultad/geográfica, cursoCargo→curso/cargoTipo/categoría/
  designaciónTipo/persona, novedad, cargoLiquidación→dependencia/categoría) y 8 adapters Feign.
- Tests: `shared/AcademicCatalogAdapterTest` (7 tests).
- Suite: **64 tests, 0 fallos**.

`SmtpBonoMailAdapter` y los puertos exclusivos de bono quedan para la ola 5, como estaba previsto.

### F2 — COMPLETADA (G2 cumplida, base)

- `infrastructure/render/pdf/PdfHeader` (record: título, período, líneas, logo, leyenda).
- `PdfHeaderFooter`: encabezado institucional + título centrado + leyenda + pie `Página N`;
  `onStartPage` **null-safe** (elimina el NPE de C11) y conserva el comportamiento de primera
  página (evento antes de `setHeader`).
- `PdfTables`: celdas sin borde / borde inferior / párrafo + fuentes.
- `PdfMerge`: unión de PDF con `newPage()` por página y cierre seguro de lectores; error de IO
  → `ReportException`.
- Tests: `PdfRenderComponentsTest` (4) — encabezado/pie, multipágina sin NPE, merge de 2 PDFs y
  celdas.
- Suite completa: **68 tests, 0 fallos**.

Nota de alcance: la paridad **exacta** de cada una de las 14 variantes de encabezado se valida
en F3, cuando cada slice se cablea al componente y corre su golden legacy.

### F3 — ola 1 COMPLETA (`cargosclase`, `cargosclaseperiodo`, `comparacionremuneraciones`, `totalesgenerales`)

**Comunes a los cuatro slices:** puertos in/out propios, caso de uso `@Component`, fachada
`@Service`, renderer con `PdfHeaderFooter` + `PdfTables`, controller con
`ReportResponseBuilder`, **borrado** del controller/service legacy y golden comparado contra
el baseline. Cada diff respecto del legacy esta declarado en
`src/test/resources/golden/expected-fixes.yml`.

**`cargosclase`**
- `reports/cargosclase/domain/model/{CargoClase,CargosClaseContent}` (records puros).
- Puertos: `GenerateCargosClaseReportUseCase` (in), `CargoClaseCatalogPort`, `CargosClasePdfRenderer` (out).
- `GenerateCargosClaseReportUseCaseImpl`: agrupa por clase, ordena cargos por nombre
  (`nullsLast`, fix C2), omite clases sin cargos, resuelve/carga el archivo por `ReportStorage`.
- `FeignCargoClaseCatalogAdapter` + `CargoClaseMapper` (lista null → vacía).
- `GenerateCargosClaseReportUseCaseTest` (2 tests).
- Paridad: `golden/cargosclase/normal` identico al baseline. Unico diff: `vacio` (C1).

**`cargosclaseperiodo`**
- Modelos: `CargoClasePeriodoDetalle`, `CargoClasePeriodoVigencia`,
  `CargoClasePeriodoDefinicion`, `CargosClasePeriodoContent` (sede → filas).
- Puertos: `GenerateCargosClasePeriodoReportUseCase` (in); `CargoClaseDetalleCatalogPort`,
  `CargoClasePeriodoCatalogPort`, `CargoClaseCatalogPort`, `CargosClasePeriodoPdfRenderer` (out).
- Caso de uso: agrupa por sede y la emite **alfabeticamente** (C4); lista null = vacia,
  vigencia/cargo/clase/sede faltante = fila omitida con `warn` (C2), `toMap` con merge (C3),
  documento siempre cerrado (C1) y facultad ausente = encabezado sin nombre (C2).
- `CargosClasePeriodoReportTest` actualizado (el caso vacio ahora es PDF valido) +
  `GenerateCargosClasePeriodoReportUseCaseTest` (3 tests).
- Paridad: unico diff funcional es el orden de sedes (`C4-order-cargosclaseperiodo`) y la
  leyenda del caso vacio (`C1-empty-cargosclaseperiodo`); filas y encabezado identicos.

**`totalesgenerales`**
- Modelos: `TotalMensualCodigo`, `CodigoGrupo`, `TotalesGeneralesContent` (grupos → filas + subtotal).
- Puertos: `GenerateTotalesGeneralesReportUseCase` (in); `TotalMensualCatalogPort`,
  `CodigoGrupoCatalogPort`, `TotalesGeneralesPdfRenderer` (out).
- Caso de uso: orden fijo Remunerativo / No Remunerativo / Deducción / Total, subtotal por
  grupo, total ausente = celda vacia (C2) y `toMap` con merge (C3).
- `GenerateTotalesGeneralesReportUseCaseTest` (2 tests).
- Paridad: `normal` identico al baseline; unico diff `vacio` (leyenda C1,
  `C1-empty-totalesgenerales`).

**`comparacionremuneraciones`**
- Modelos: `LiquidacionRemuneracion`, `ComparacionRemuneracionesContent`
  (dependencia → filas con bruto/neto anterior, actual, diferencia y porcentaje).
- Puertos: `GenerateComparacionRemuneracionesReportUseCase` (in);
  `LiquidacionAcreditadoCatalogPort`, `ComparacionRemuneracionesPdfRenderer` (out).
- Caso de uso: dependencias por id ascendente y personas por apellido/nombre; sin liquidacion
  actual se omite, sin anterior se compara contra cero; totales null = cero (C2); maps con
  merge (C3); el renderer propaga `ReportException` en vez de devolver 200 con PDF vacio (C20).
- `ComparacionRemuneracionesReportTest` actualizado (vacio = PDF valido) +
  `GenerateComparacionRemuneracionesReportUseCaseTest` (3 tests).
- Paridad: `normal` **identico** al baseline (incluido el encabezado con
  `Actual: m/aaaa    Anterior: m/aaaa`); unico diff `vacio` (C1/C20,
  `C1-empty-comparacionremuneraciones`).

**Componentes compartidos extendidos (sin cambiar slices ya migrados):**
- `PdfHeaderFooter` emite el periodo antes de las lineas adicionales y acepta un `subtitle`
  centrado debajo del titulo (`PdfHeader.subtitle`), necesario para el encabezado de
  `comparacionremuneraciones`.
- `PdfRenderComponentsTest` ajustado al nuevo constructor de `PdfHeader`.

**Puerta G3.1:** `mvn -o verify` verde, **78 tests, 0 fallos**. `endpoints.txt` y
`coverage.txt` siguen cubriendo los 26 endpoints sin cambios de ruta ni de headers.

### F3 — ola 2 COMPLETA (`cursodocente`, `designacionesdocente`, `docentessede`, `fusiondocente`, `novedadesdocentes`, `historicoasignacioncargos`)

- Se migraron los seis slices con puertos in/out, caso de uso, fachada, adapter Feign,
  renderer y controller nuevos; se borraron los `service`/`controller` legacy.
- `docentessede`: sin estado mutable compartido (C5), nombre interno sin repetir facultad
  (C8) y `ReportException` si falta facultad o sede (C19).
- `novedadesdocentes`: encabezado inline (institucion + facultad + titulo + periodo) y
  bloques ALTA/CAMBIO/BAJA; sin curso/persona/cargo se emiten celdas vacias (C2).
- `historicoasignacioncargos`: único cambio funcional es el typo C17; sigue consultando los
  cuatro catálogos y emitiendo sólo encabezado.
- `fusiondocente`: una sola lectura de cada catálogo y `toMap` con merge (C2/C3).
- `PdfHeader` ganó `linesBeforePeriod` para los encabezados inline, preservando la paridad
  exacta del texto.
- **G3.2:** suite verde; diffs declarados: C1 en los cinco vacíos y C17 en histórico.

### F3 — ola 3 COMPLETA (`cursocargonovedad`, `designacionesdetalle`, `anotador`)

- `cursocargonovedad`: tabla landscape de 11 columnas, tipos/estados y horas calculados en el
  caso de uso; dos endpoints inline con nombres `novedades-cursos…`.
- `designacionesdetalle`: contenido con liquidadas del mes anterior, cursos, fusiones y
  resumen; puerto propio para la novedad de código 27 y para `designacionTipo`.
- `anotador`: un único caso de uso para las variantes facultad/período y
  pendientes/revisados; nombres internos distintos (C16), sin `null` literal (C14), fecha
  formateada (C15) y título "Rechazados" condicionado (C13).
- **G3.3:** suite verde; diffs declarados para C13/C14/C15/C1 en anotador.

### F3 — ola 4 (`sheet`) y ola 5 (`bono`)

- `sheet`: XLSX con una fila por curso-cargo; catálogos en memoria, `toMap` con merge (C3) y
  celdas vacías cuando falta una referencia (C2).
- `bono`: puerta única `BonoDataPort` para las lecturas/escrituras al core, `BonoMailPort`
  para SMTP, cuatro casos de uso (recibo, detalle, lote por dependencia y envío) y dos
  renderers. Se corrigieron C1, C2, C3, C6, C7, C9 y C12; la auditoría ocurre después del
  render y con `Clock` inyectable.
- `NumerosEnLetras` reemplaza `Tool.number_2_text` con tabla de tests propia.
- **G3.4/G3.5:** suite verde; diffs declarados por C12 (cabecera duplicada) en los goldens
  del bono.

### F4 — Limpieza COMPLETA

- Se borraron `Tool`, `Jsonifier`, `Jsonifyable` y los paquetes `service`, `controller`,
  `model`, `client` y `util`.
- Se activó `HexagonalRulesTest`: sin imports entre slices, `shared` no importa slices,
  dominio sin frameworks, aplicación sin infraestructura/OpenPDF/POI/Mail, sin
  `@RequestParam`/`@Query`/`@ModelAttribute` y sin paquetes legacy.
- `GenerateBonosDependenciaUseCaseImpl` usa el puerto `PdfMergePort` (el adapter envuelve
  `PdfMerge`), manteniendo la regla de dependencias.
- **G4:** `mvn -o verify` verde.

### F5 — Documentación COMPLETA

- README con la arquitectura hexagonal y los 17 slices.
- `docs/diagrams/arquitectura-general.mmd` y `flujo-reportes.mmd` actualizados.
- `REFACTOR_HEXAGONAL_NOTES.md` con el mapa C1–C20 → implementación.
- CHANGELOG `[Unreleased]` con el alcance del refactor; al cerrar la release ese contenido
  pasa a `[0.7.0] - 2026-10-24` y `pom.xml` se actualiza a `0.7.0`.
- **Sin commit, tag ni push** (D7).

### Estado final de la suite

`mvn -o verify`: **88 tests, 0 fallos**. Los 26 endpoints mantienen ruta, verbo, headers y
nombre de archivo; las únicas diferencias contra el baseline legacy son las declaradas en
`src/test/resources/golden/expected-fixes.yml`.
