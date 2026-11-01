package um.haberes.report.reports.comparacionremuneraciones;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.DependenciaDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.LiquidacionDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.PersonaDto;
import um.haberes.report.support.AbstractReportIntegrationTest;
import um.haberes.report.support.Fixtures;
import um.haberes.report.support.GoldenFiles;
import um.haberes.report.support.ReportHttp;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Reporte "Comparación de Remuneraciones" ({@code GET /comparacion/comparacionRemuneraciones/...}).
 *
 * <p>Contrato: PDF attachment {@code comparacion-remuneraciones.pdf}. Contenido: por dependencia
 * (ordenada por id) las personas con liquidación en el período actual, ordenadas por apellido y
 * nombre, con bruto/neto anterior, actual y diferencia; la persona sin liquidación anterior se
 * compara contra cero y la persona sin liquidación actual no se imprime.
 */
class ComparacionRemuneracionesReportTest extends AbstractReportIntegrationTest {

    private static final String URL =
            "/api/haberes/report/comparacion/comparacionRemuneraciones/2026/9/2026/8";

    @Test
    void generaPdfConComparacionPorDependencia() throws Exception {
        JsonNode fixture = Fixtures.node("comparacionremuneraciones", "normal");
        stub(fixture);

        MvcResult result = mockMvc.perform(get(URL)).andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "comparacion-remuneraciones.pdf");
        GoldenFiles.assertPdf("comparacionremuneraciones", "normal", pdf);
    }

    /**
     * Sin liquidaciones del período actual se emite un PDF valido con el encabezado y la
     * leyenda "Sin datos para el período." (C1), en lugar del archivo de 0 bytes del legacy.
     */
    @Test
    void generaPdfValidoSinDatos() throws Exception {
        JsonNode fixture = Fixtures.node("comparacionremuneraciones", "vacio");
        stub(fixture);

        MvcResult result = mockMvc.perform(get(URL)).andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "comparacion-remuneraciones.pdf");
        GoldenFiles.assertPdf("comparacionremuneraciones", "vacio", pdf);
    }

    private void stub(JsonNode fixture) {
        when(liquidacionClient.findAllByAcreditado(2026, 9))
                .thenReturn(Fixtures.list(fixture, "liquidacionesActual", LiquidacionDto.class));
        when(liquidacionClient.findAllByAcreditado(2026, 8))
                .thenReturn(Fixtures.list(fixture, "liquidacionesAnterior", LiquidacionDto.class));
        when(personaClient.findAll())
                .thenReturn(Fixtures.list(fixture, "personas", PersonaDto.class));
        when(dependenciaClient.findAll())
                .thenReturn(Fixtures.list(fixture, "dependencias", DependenciaDto.class));
    }
}
