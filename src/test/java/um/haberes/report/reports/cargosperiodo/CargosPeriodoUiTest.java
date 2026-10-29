package um.haberes.report.reports.cargosperiodo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CargoLiquidacionDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.PersonaDto;
import um.haberes.report.support.AbstractReportIntegrationTest;
import um.haberes.report.support.Fixtures;
import um.haberes.report.support.GoldenFiles;
import um.haberes.report.support.ReportHttp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/** Namespace del frontend para la "Planilla de Cargos por Período". */
class CargosPeriodoUiTest extends AbstractReportIntegrationTest {

    private static final String BASE = "/api/haberes/report/ui/cargosPeriodo";
    private static final String API_KEY = "test-api-key";
    private static final String HEADER = "X-API-Key";

    @Test
    void devuelveLosCargosDelPeriodo() throws Exception {
        stubNormal();

        MvcResult result = mockMvc.perform(get(BASE + "/data/2026/9").header(HEADER, API_KEY))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        var json = new ObjectMapper().readTree(result.getResponse().getContentAsString());
        assertThat(json.get("filas")).hasSize(2);
        var gomez = json.get("filas").get(0);
        assertThat(gomez.get("legajoId").asText()).isEqualTo("1001");
        assertThat(gomez.get("periodo").asText()).isEqualTo("9/2026");
        assertThat(gomez.get("dependenciaNombre").asText()).isEqualTo("Facultad de Ingeniería");
        assertThat(gomez.get("categoriaNombre").asText()).isEqualTo("Profesor Titular");
    }

    @Test
    void descargaElPdfDelPeriodo() throws Exception {
        stubNormal();

        MvcResult result = mockMvc.perform(get(BASE + "/pdf/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "cargosperiodo.pdf");
        GoldenFiles.assertPdf("cargosperiodo", "normal", pdf);
    }

    @Test
    void descargaElXlsxDelPeriodo() throws Exception {
        stubNormal();

        MvcResult result = mockMvc.perform(get(BASE + "/xlsx/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] xlsx = ReportHttp.assertAttachment(result, "cargosperiodo.xlsx");
        GoldenFiles.assertWorkbook("cargosperiodo", "normal-xlsx", xlsx);
    }

    @Test
    void descargaElPdfSinDatos() throws Exception {
        stubVacio();

        MvcResult result = mockMvc.perform(get(BASE + "/pdf/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "cargosperiodo.pdf");
        GoldenFiles.assertPdf("cargosperiodo", "vacio", pdf);
    }

    @Test
    void descargaElXlsxSinDatos() throws Exception {
        stubVacio();

        MvcResult result = mockMvc.perform(get(BASE + "/xlsx/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] xlsx = ReportHttp.assertAttachment(result, "cargosperiodo.xlsx");
        GoldenFiles.assertWorkbook("cargosperiodo", "vacio-xlsx", xlsx);
    }

    private void stubNormal() {
        JsonNode fixture = Fixtures.node("cargosperiodo", "normal");
        when(cargoLiquidacionClient.findAllActivosByPeriodo(2026, 9))
                .thenReturn(Fixtures.list(fixture, "cargos", CargoLiquidacionDto.class));
        when(personaClient.findAllLegajos(anyList()))
                .thenReturn(Fixtures.list(fixture, "personas", PersonaDto.class));
    }

    private void stubVacio() {
        JsonNode fixture = Fixtures.node("cargosperiodo", "vacio");
        when(cargoLiquidacionClient.findAllActivosByPeriodo(2026, 9))
                .thenReturn(Fixtures.list(fixture, "cargos", CargoLiquidacionDto.class));
    }
}
