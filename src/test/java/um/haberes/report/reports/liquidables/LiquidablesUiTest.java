package um.haberes.report.reports.liquidables;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.PersonaDto;
import um.haberes.report.support.AbstractReportIntegrationTest;
import um.haberes.report.support.Fixtures;
import um.haberes.report.support.GoldenFiles;
import um.haberes.report.support.ReportHttp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/** Namespace del frontend para la "Planilla de Liquidables". */
class LiquidablesUiTest extends AbstractReportIntegrationTest {

    private static final String BASE = "/api/haberes/report/ui/liquidables";
    private static final String API_KEY = "test-api-key";
    private static final String HEADER = "X-API-Key";

    @Test
    void devuelveLasPersonasLiquidables() throws Exception {
        stub(Fixtures.node("liquidables", "normal"));

        MvcResult result = mockMvc.perform(get(BASE + "/data").header(HEADER, API_KEY))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        var json = new ObjectMapper().readTree(result.getResponse().getContentAsString());
        assertThat(json.get("filas")).hasSize(3);
        var gomez = json.get("filas").get(0);
        assertThat(gomez.get("legajoId").asText()).isEqualTo("1001");
        assertThat(gomez.get("apellidoNombre").asText()).isEqualTo("Gómez, Ana");
        assertThat(gomez.get("estado").asText()).isEqualTo("1");
        assertThat(gomez.get("dependenciaNombre").asText()).isEqualTo("Facultad de Ingeniería");
        assertThat(json.get("filas").get(2).get("dependenciaNombre").asText()).isEmpty();
    }

    @Test
    void descargaElPdf() throws Exception {
        stub(Fixtures.node("liquidables", "normal"));

        MvcResult result = mockMvc.perform(get(BASE + "/pdf").header(HEADER, API_KEY))
                .andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "liquidables.pdf");
        GoldenFiles.assertPdf("liquidables", "normal", pdf);
    }

    @Test
    void descargaElXlsx() throws Exception {
        stub(Fixtures.node("liquidables", "normal"));

        MvcResult result = mockMvc.perform(get(BASE + "/xlsx").header(HEADER, API_KEY))
                .andReturn();

        byte[] xlsx = ReportHttp.assertAttachment(result, "liquidables.xlsx");
        GoldenFiles.assertWorkbook("liquidables", "normal-xlsx", xlsx);
    }

    @Test
    void descargaElPdfSinDatos() throws Exception {
        stub(Fixtures.node("liquidables", "vacio"));

        MvcResult result = mockMvc.perform(get(BASE + "/pdf").header(HEADER, API_KEY))
                .andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "liquidables.pdf");
        GoldenFiles.assertPdf("liquidables", "vacio", pdf);
    }

    @Test
    void descargaElXlsxSinDatos() throws Exception {
        stub(Fixtures.node("liquidables", "vacio"));

        MvcResult result = mockMvc.perform(get(BASE + "/xlsx").header(HEADER, API_KEY))
                .andReturn();

        byte[] xlsx = ReportHttp.assertAttachment(result, "liquidables.xlsx");
        GoldenFiles.assertWorkbook("liquidables", "vacio-xlsx", xlsx);
    }

    private void stub(JsonNode fixture) {
        when(personaClient.findAllLiquidables())
                .thenReturn(Fixtures.list(fixture, "personas", PersonaDto.class));
    }
}
