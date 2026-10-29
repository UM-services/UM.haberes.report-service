package um.haberes.report.reports.totalesgenerales;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CodigoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CodigoGrupoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.TotalMensualDto;
import um.haberes.report.support.AbstractReportIntegrationTest;
import um.haberes.report.support.Fixtures;
import um.haberes.report.support.GoldenFiles;
import um.haberes.report.support.ReportHttp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Namespace nuevo del frontend ({@code /api/haberes/report/ui/**}): tabla, PDF y XLSX del
 * mismo contenido, con la API key que inyecta el gateway.
 */
class TotalesGeneralesUiTest extends AbstractReportIntegrationTest {

    private static final String BASE = "/api/haberes/report/ui/totalesGenerales";
    private static final String API_KEY = "test-api-key";
    private static final String HEADER = "X-API-Key";

    @Test
    void devuelveLosTotalesAgrupados() throws Exception {
        stub(Fixtures.node("totalesgenerales", "normal"));

        MvcResult result = mockMvc.perform(get(BASE + "/data/2026/9").header(HEADER, API_KEY))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getResponse().getContentType()).contains("application/json");

        var json = new ObjectMapper().readTree(result.getResponse().getContentAsString());
        assertThat(json.get("anho").asInt()).isEqualTo(2026);
        assertThat(json.get("mes").asInt()).isEqualTo(9);
        assertThat(json.get("grupos")).hasSize(4);
        assertThat(json.get("grupos").get(0).get("nombre").asText()).isEqualTo("Remunerativo");
        assertThat(json.get("grupos").get(0).get("filas").get(0).get("codigoId").asText())
                .isEqualTo("100");
        assertThat(json.get("grupos").get(0).get("filas").get(0).get("total").asText())
                .isEqualTo("1234567.89");
        assertThat(json.get("grupos").get(0).get("subtotal").asText())
                .isEqualTo("1.333.333,32");
        assertThat(json.get("grupos").get(2).get("nombre").asText()).isEqualTo("Deducción");
        assertThat(json.get("grupos").get(2).get("filas").get(0).get("total").asText())
                .isEqualTo("-45678.9");
    }

    @Test
    void devuelveListaVaciaSinDatos() throws Exception {
        stub(Fixtures.node("totalesgenerales", "vacio"));

        MvcResult result = mockMvc.perform(get(BASE + "/data/2026/9").header(HEADER, API_KEY))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        var json = new ObjectMapper().readTree(result.getResponse().getContentAsString());
        assertThat(json.get("grupos")).isEmpty();
    }

    @Test
    void descargaElPdfDelPeriodo() throws Exception {
        stub(Fixtures.node("totalesgenerales", "normal"));

        MvcResult result = mockMvc.perform(get(BASE + "/pdf/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "totales_generales.pdf");
        GoldenFiles.assertPdf("totalesgenerales", "normal", pdf);
    }

    @Test
    void descargaElXlsxDelPeriodo() throws Exception {
        stub(Fixtures.node("totalesgenerales", "normal"));

        MvcResult result = mockMvc.perform(get(BASE + "/xlsx/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] xlsx = ReportHttp.assertAttachment(result, "totales_generales.xlsx");
        GoldenFiles.assertWorkbook("totalesgenerales", "normal-xlsx", xlsx);
    }

    @Test
    void descargaElXlsxSinDatos() throws Exception {
        stub(Fixtures.node("totalesgenerales", "vacio"));

        MvcResult result = mockMvc.perform(get(BASE + "/xlsx/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] xlsx = ReportHttp.assertAttachment(result, "totales_generales.xlsx");
        GoldenFiles.assertWorkbook("totalesgenerales", "vacio-xlsx", xlsx);
    }

    private void stub(com.fasterxml.jackson.databind.JsonNode fixture) {
        when(totalMensualClient.findAllByPeriodo(2026, 9))
                .thenReturn(Fixtures.list(fixture, "totalMensuales", TotalMensualDto.class));
        when(codigoClient.findAllByPeriodo(2026, 9))
                .thenReturn(Fixtures.list(fixture, "codigos", CodigoDto.class));
        when(codigoGrupoClient.findAll())
                .thenReturn(Fixtures.list(fixture, "codigoGrupos", CodigoGrupoDto.class));
    }
}
