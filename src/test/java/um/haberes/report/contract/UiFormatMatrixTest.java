package um.haberes.report.contract;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Matriz de formatos por reporte (plan §8.5), verificada contra el manifiesto de endpoints.
 *
 * <p>Cada pantalla del menú Consultas entrega tabla (`data`) y descargas. Las excepciones
 * documentadas son: bonos por dependencia (grilla propia + PDF consolidado + envío, sin XLSX),
 * histórico de cargos (aviso + PDF, sin XLSX) y bono individual (sólo acciones de PDF/envío,
 * sin grilla). Libro de sueldos suma `lsd` (ZIP del Libro de Sueldos Digital).
 *
 * <p>Si se agrega, quita o renombra una ruta sin actualizar esta matriz, el test falla.
 */
class UiFormatMatrixTest {

    /** reporte -> recursos publicados bajo {@code /api/haberes/report/ui/<reporte>/}. */
    private static final Map<String, Set<String>> ESPERADO = Map.ofEntries(
            Map.entry("totalesGenerales", Set.of("data", "pdf", "xlsx")),
            Map.entry("totalesCodigo", Set.of("data", "pdf", "xlsx")),
            Map.entry("comparacionCodigos", Set.of("data", "pdf", "xlsx")),
            Map.entry("comparacionRemuneraciones", Set.of("data", "pdf", "xlsx")),
            Map.entry("detalleCodigo", Set.of("data", "pdf", "xlsx")),
            Map.entry("libroSueldos", Set.of("data", "pdf", "xlsx", "lsd")),
            Map.entry("docentesSede", Set.of("data", "pdf", "xlsx")),
            Map.entry("designacionesDocente", Set.of("data", "pdf", "xlsx")),
            Map.entry("cursoDocente", Set.of("data", "pdf", "xlsx")),
            Map.entry("cursosContratado", Set.of("data", "pdf", "xlsx")),
            Map.entry("fusionDocente", Set.of("data", "pdf", "xlsx")),
            Map.entry("liquidables", Set.of("data", "pdf", "xlsx")),
            Map.entry("cargosPeriodo", Set.of("data", "pdf", "xlsx")),
            Map.entry("cursosPeriodo", Set.of("data", "pdf", "xlsx")),
            Map.entry("basicosPeriodo", Set.of("data", "pdf", "xlsx")),
            Map.entry("cantidadCargos", Set.of("data", "pdf", "xlsx")),
            Map.entry("cargosClasePeriodo", Set.of("data", "pdf", "xlsx")),
            Map.entry("historicoCargos", Set.of("data", "pdf")),
            Map.entry("bonoDependencia", Set.of("data", "pdf", "sendBono")),
            Map.entry("bono", Set.of("generatePdf", "sendBono")));

    @Test
    void cadaReportePublicaLosFormatosDefinidosEnLaMatriz() {
        Map<String, Set<String>> publicado = recursosPublicados();

        assertThat(publicado.keySet())
                .as("reportes con endpoints en /ui")
                .containsExactlyInAnyOrderElementsOf(ESPERADO.keySet());

        ESPERADO.forEach((reporte, recursos) -> assertThat(publicado.get(reporte))
                .as("recursos publicados por %s", reporte)
                .containsExactlyInAnyOrderElementsOf(recursos));
    }

    private Map<String, Set<String>> recursosPublicados() {
        Map<String, Set<String>> recursos = new LinkedHashMap<>();
        for (String linea : lineas("/contract/endpoints.txt")) {
            String ruta = linea.substring(linea.indexOf(' ') + 1);
            String prefijo = "/api/haberes/report/ui/";
            if (!ruta.startsWith(prefijo)) {
                continue;
            }
            String[] partes = ruta.substring(prefijo.length()).split("/");
            recursos.computeIfAbsent(partes[0], reporte -> new TreeSet<>()).add(partes[1]);
        }
        return recursos;
    }

    private Set<String> lineas(String resource) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream(resource)))) {
            return reader.lines()
                    .map(String::trim)
                    .filter(linea -> !linea.isEmpty())
                    .collect(Collectors.toCollection(TreeSet::new));
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer " + resource, e);
        }
    }
}
