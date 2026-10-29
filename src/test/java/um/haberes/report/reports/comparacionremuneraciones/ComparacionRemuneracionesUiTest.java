package um.haberes.report.reports.comparacionremuneraciones;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.DependenciaDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.LiquidacionDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.PersonaDto;
import um.haberes.report.support.AbstractReportIntegrationTest;
import um.haberes.report.support.Fixtures;
import um.haberes.report.support.GoldenFiles;
import um.haberes.report.support.ReportHttp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/** Namespace del frontend para "Comparación de Remuneraciones". */
class ComparacionRemuneracionesUiTest extends AbstractReportIntegrationTest {

    private static final String BASE = "/api/haberes/report/ui/comparacionRemuneraciones";
    private static final String API_KEY = "test-api-key";
    private static final String HEADER = "X-API-Key";

    @Test
    void devuelveLaComparacionPorDependencia() throws Exception {
        stub(Fixtures.node("comparacionremuneraciones", "normal"));

        MvcResult result = mockMvc
                .perform(get(BASE + "/data/2026/9/2026/8").header(HEADER, API_KEY))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        var json = new ObjectMapper().readTree(result.getResponse().getContentAsString());
        assertThat(json.get("anho").asInt()).isEqualTo(2026);
        assertThat(json.get("mes").asInt()).isEqualTo(9);
        assertThat(json.get("anhoAnterior").asInt()).isEqualTo(2026);
        assertThat(json.get("mesAnterior").asInt()).isEqualTo(8);
        assertThat(json.get("dependencias")).hasSize(2);
        assertThat(json.get("dependencias").get(0).get("nombre").asText()).isEqualTo("Rectorado");
        assertThat(json.get("dependencias").get(0).get("filas")).hasSize(2);
        assertThat(json.get("dependencias").get(1).get("nombre").asText())
                .isEqualTo("Facultad de Ingeniería");
        var gomez = json.get("dependencias").get(0).get("filas").get(0);
        assertThat(gomez.get("apellidoNombre").asText()).isEqualTo("Gómez, Ana");
        assertThat(gomez.get("brutoPorcentaje").asText()).isEqualTo("7,14%");
        assertThat(gomez.get("netoDiferencia").asText()).isEqualTo("100.000");
    }

    @Test
    void devuelveDependenciasVaciasSinDatos() throws Exception {
        stub(Fixtures.node("comparacionremuneraciones", "vacio"));

        MvcResult result = mockMvc
                .perform(get(BASE + "/data/2026/9/2026/8").header(HEADER, API_KEY))
                .andReturn();

        var json = new ObjectMapper().readTree(result.getResponse().getContentAsString());
        assertThat(json.get("dependencias")).isEmpty();
    }

    @Test
    void descargaElPdfDelPeriodo() throws Exception {
        stub(Fixtures.node("comparacionremuneraciones", "normal"));

        MvcResult result = mockMvc
                .perform(get(BASE + "/pdf/2026/9/2026/8").header(HEADER, API_KEY))
                .andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "comparacion-remuneraciones.pdf");
        GoldenFiles.assertPdf("comparacionremuneraciones", "normal", pdf);
    }

    @Test
    void descargaElXlsxDelPeriodo() throws Exception {
        stub(Fixtures.node("comparacionremuneraciones", "normal"));

        MvcResult result = mockMvc
                .perform(get(BASE + "/xlsx/2026/9/2026/8").header(HEADER, API_KEY))
                .andReturn();

        byte[] xlsx = ReportHttp.assertAttachment(result, "comparacion-remuneraciones.xlsx");
        GoldenFiles.assertWorkbook("comparacionremuneraciones", "normal-xlsx", xlsx);
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
