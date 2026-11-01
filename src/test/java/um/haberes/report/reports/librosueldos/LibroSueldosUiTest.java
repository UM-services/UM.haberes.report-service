package um.haberes.report.reports.librosueldos;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.LiquidacionDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.PersonaDto;
import um.haberes.report.support.AbstractReportIntegrationTest;
import um.haberes.report.support.Fixtures;
import um.haberes.report.support.GoldenFiles;
import um.haberes.report.support.ReportHttp;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/** Namespace del frontend para el "Libro de Sueldos" (tabla, PDF, XLSX y ZIP del LSD). */
class LibroSueldosUiTest extends AbstractReportIntegrationTest {

    private static final String BASE = "/api/haberes/report/ui/libroSueldos";
    private static final String API_KEY = "test-api-key";
    private static final String HEADER = "X-API-Key";

    @Test
    void devuelveElLibroDeSueldosDelPeriodo() throws Exception {
        stubNormal();

        MvcResult result = mockMvc.perform(get(BASE + "/data/2026/9").header(HEADER, API_KEY))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        var json = new ObjectMapper().readTree(result.getResponse().getContentAsString());
        assertThat(json.get("filas")).hasSize(2);
        var gomez = json.get("filas").get(0);
        assertThat(gomez.get("legajoId").asText()).isEqualTo("1001");
        assertThat(gomez.get("cuil").asText()).isEqualTo("27-20111222-4");
        assertThat(gomez.get("dependencia").asText()).isEqualTo("Facultad de Ingeniería");
        assertThat(gomez.get("neto").asText()).isEqualTo("1.250.000,25");
        assertThat(json.get("filas").get(1).get("dependencia").asText()).isEmpty();
    }

    @Test
    void descargaElPdfDelPeriodo() throws Exception {
        stubNormal();

        MvcResult result = mockMvc.perform(get(BASE + "/pdf/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "librosueldos.pdf");
        GoldenFiles.assertPdf("librosueldos", "normal", pdf);
    }

    @Test
    void descargaElXlsxDelPeriodo() throws Exception {
        stubNormal();

        MvcResult result = mockMvc.perform(get(BASE + "/xlsx/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] xlsx = ReportHttp.assertAttachment(result, "librosueldos.xlsx");
        GoldenFiles.assertWorkbook("librosueldos", "normal-xlsx", xlsx);
    }

    @Test
    void descargaElZipDelLibroDeSueldosDigital() throws Exception {
        byte[] zip = "%PDF-fake-zip".getBytes();
        when(libroSueldoClient.generate(2026, 9, List.of(1001L, 1002L))).thenReturn(zip);

        MvcResult result = mockMvc.perform(post(BASE + "/lsd/2026/9")
                        .header(HEADER, API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[1001,1002]"))
                .andReturn();

        byte[] body = ReportHttp.assertAttachment(result, "LSD.zip");
        assertThat(body).isEqualTo(zip);
    }

    @Test
    void descargaElPdfSinDatos() throws Exception {
        stubVacio();

        MvcResult result = mockMvc.perform(get(BASE + "/pdf/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "librosueldos.pdf");
        GoldenFiles.assertPdf("librosueldos", "vacio", pdf);
    }

    private void stubNormal() {
        JsonNode fixture = Fixtures.node("librosueldos", "normal");
        when(liquidacionClient.findAllByPeriodo(2026, 9, 0))
                .thenReturn(Fixtures.list(fixture, "liquidaciones", LiquidacionDto.class));
        when(personaClient.findAllLegajos(anyList()))
                .thenReturn(Fixtures.list(fixture, "personas", PersonaDto.class));
    }

    private void stubVacio() {
        JsonNode fixture = Fixtures.node("librosueldos", "vacio");
        when(liquidacionClient.findAllByPeriodo(2026, 9, 0))
                .thenReturn(Fixtures.list(fixture, "liquidaciones", LiquidacionDto.class));
    }
}
