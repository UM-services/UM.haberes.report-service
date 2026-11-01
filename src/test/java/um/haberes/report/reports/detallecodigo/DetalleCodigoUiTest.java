package um.haberes.report.reports.detallecodigo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CodigoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.ItemDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.NovedadAcumuladoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.PersonaDto;
import um.haberes.report.support.AbstractReportIntegrationTest;
import um.haberes.report.support.Fixtures;
import um.haberes.report.support.GoldenFiles;
import um.haberes.report.support.ReportHttp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/** Namespace del frontend para el "Detalle por Código". */
class DetalleCodigoUiTest extends AbstractReportIntegrationTest {

    private static final String BASE = "/api/haberes/report/ui/detalleCodigo";
    private static final String API_KEY = "test-api-key";
    private static final String HEADER = "X-API-Key";

    @Test
    void devuelveElDetalleDelCodigo() throws Exception {
        stubNormal();

        MvcResult result = mockMvc.perform(get(BASE + "/data/31/2026/9").header(HEADER, API_KEY))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        var json = new ObjectMapper().readTree(result.getResponse().getContentAsString());
        assertThat(json.get("codigoId").asText()).isEqualTo("31");
        assertThat(json.get("codigoNombre").asText()).isEqualTo("Antigüedad");
        assertThat(json.get("filas")).hasSize(3);
        var gomez = json.get("filas").get(0);
        assertThat(gomez.get("legajoId").asText()).isEqualTo("1001");
        assertThat(gomez.get("liquidado").asText()).isEqualTo("123.456,78");
        assertThat(gomez.get("novedad").asText()).isEqualTo("1.000,50");
        assertThat(gomez.get("cantidad").asText()).isEqualTo("2");
        var perez = json.get("filas").get(1);
        assertThat(perez.get("novedad").asText()).isEmpty();
        assertThat(perez.get("cantidad").asText()).isEmpty();
        var lopez = json.get("filas").get(2);
        assertThat(lopez.get("liquidado").asText()).isEmpty();
        assertThat(lopez.get("novedad").asText()).isEqualTo("250,00");
    }

    @Test
    void descargaElPdfDelPeriodo() throws Exception {
        stubNormal();

        MvcResult result = mockMvc.perform(get(BASE + "/pdf/31/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "detallecodigo.pdf");
        GoldenFiles.assertPdf("detallecodigo", "normal", pdf);
    }

    @Test
    void descargaElXlsxDelPeriodo() throws Exception {
        stubNormal();

        MvcResult result = mockMvc.perform(get(BASE + "/xlsx/31/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] xlsx = ReportHttp.assertAttachment(result, "detallecodigo.xlsx");
        GoldenFiles.assertWorkbook("detallecodigo", "normal-xlsx", xlsx);
    }

    @Test
    void descargaElPdfSinDatos() throws Exception {
        stubVacio();

        MvcResult result = mockMvc.perform(get(BASE + "/pdf/31/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "detallecodigo.pdf");
        GoldenFiles.assertPdf("detallecodigo", "vacio", pdf);
    }

    @Test
    void descargaElXlsxSinDatos() throws Exception {
        stubVacio();

        MvcResult result = mockMvc.perform(get(BASE + "/xlsx/31/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] xlsx = ReportHttp.assertAttachment(result, "detallecodigo.xlsx");
        GoldenFiles.assertWorkbook("detallecodigo", "vacio-xlsx", xlsx);
    }

    private void stubNormal() {
        JsonNode fixture = Fixtures.node("detallecodigo", "normal");
        when(codigoClient.findByCodigoId(31))
                .thenReturn(Fixtures.one(fixture, "codigo", CodigoDto.class));
        when(itemClient.findAllByCodigoId(31, 2026, 9))
                .thenReturn(Fixtures.list(fixture, "items", ItemDto.class));
        when(novedadAcumuladoClient.findAllByCodigo(31, 2026, 9))
                .thenReturn(Fixtures.list(fixture, "novedades", NovedadAcumuladoDto.class));
        when(personaClient.findAllByLiquidado(2026, 9))
                .thenReturn(Fixtures.list(fixture, "personas", PersonaDto.class));
    }

    private void stubVacio() {
        JsonNode fixture = Fixtures.node("detallecodigo", "vacio");
        when(codigoClient.findByCodigoId(31))
                .thenReturn(Fixtures.one(fixture, "codigo", CodigoDto.class));
        when(itemClient.findAllByCodigoId(31, 2026, 9))
                .thenReturn(Fixtures.list(fixture, "items", ItemDto.class));
        when(novedadAcumuladoClient.findAllByCodigo(31, 2026, 9))
                .thenReturn(Fixtures.list(fixture, "novedades", NovedadAcumuladoDto.class));
        when(personaClient.findAllByLiquidado(2026, 9))
                .thenReturn(Fixtures.list(fixture, "personas", PersonaDto.class));
    }
}
