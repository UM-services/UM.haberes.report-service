package um.haberes.report.reports.totalescodigo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CodigoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.TotalItemDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.TotalNovedadDto;
import um.haberes.report.support.AbstractReportIntegrationTest;
import um.haberes.report.support.Fixtures;
import um.haberes.report.support.GoldenFiles;
import um.haberes.report.support.ReportHttp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/** Namespace del frontend para "Totales Generales por Código". */
class TotalesCodigoUiTest extends AbstractReportIntegrationTest {

    private static final String BASE = "/api/haberes/report/ui/totalesCodigo";
    private static final String API_KEY = "test-api-key";
    private static final String HEADER = "X-API-Key";

    @Test
    void devuelveSoloLosCodigosConNovedad() throws Exception {
        stub(Fixtures.node("totalescodigo", "normal"));

        MvcResult result = mockMvc.perform(get(BASE + "/data/2026/9").header(HEADER, API_KEY))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        var json = new ObjectMapper().readTree(result.getResponse().getContentAsString());
        assertThat(json.get("filas")).hasSize(3);
        assertThat(json.get("filas").get(0).get("codigoId").asInt()).isEqualTo(10);
        assertThat(json.get("filas").get(0).get("nombre").asText()).isEqualTo("Sueldo Básico");
        assertThat(json.get("filas").get(0).get("novedades").asText()).isEqualTo("1.500.000,5");
        assertThat(json.get("filas").get(0).get("liquidado").asText()).isEqualTo("1.499.999,5");
        assertThat(json.get("filas").get(0).get("diferencia").asText()).isEqualTo("1");
        assertThat(json.get("filas").get(2).get("codigoId").asInt()).isEqualTo(40);
    }

    @Test
    void devuelveListaVaciaSinDatos() throws Exception {
        stub(Fixtures.node("totalescodigo", "vacio"));

        MvcResult result = mockMvc.perform(get(BASE + "/data/2026/9").header(HEADER, API_KEY))
                .andReturn();

        var json = new ObjectMapper().readTree(result.getResponse().getContentAsString());
        assertThat(json.get("filas")).isEmpty();
    }

    @Test
    void descargaElPdfDelPeriodo() throws Exception {
        stub(Fixtures.node("totalescodigo", "normal"));

        MvcResult result = mockMvc.perform(get(BASE + "/pdf/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "totales_codigo.pdf");
        GoldenFiles.assertPdf("totalescodigo", "normal", pdf);
    }

    @Test
    void descargaElXlsxDelPeriodo() throws Exception {
        stub(Fixtures.node("totalescodigo", "normal"));

        MvcResult result = mockMvc.perform(get(BASE + "/xlsx/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] xlsx = ReportHttp.assertAttachment(result, "totales_codigo.xlsx");
        GoldenFiles.assertWorkbook("totalescodigo", "normal-xlsx", xlsx);
    }

    private void stub(JsonNode fixture) {
        when(totalNovedadClient.findAllByPeriodo(2026, 9))
                .thenReturn(Fixtures.list(fixture, "totalNovedades", TotalNovedadDto.class));
        when(totalItemClient.findAllByPeriodo(2026, 9))
                .thenReturn(Fixtures.list(fixture, "totalItems", TotalItemDto.class));
        when(codigoClient.findAllByPeriodo(2026, 9))
                .thenReturn(Fixtures.list(fixture, "codigos", CodigoDto.class));
    }
}
