package um.haberes.report.reports.cantidadcargos;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.FacultadDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.GeograficaDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.ItemDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.LegajoCursoCantidadDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.PersonaDto;
import um.haberes.report.support.AbstractReportIntegrationTest;
import um.haberes.report.support.Fixtures;
import um.haberes.report.support.GoldenFiles;
import um.haberes.report.support.ReportHttp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/** Namespace del frontend para la "Planilla de Cantidad de Cargos". */
class CantidadCargosUiTest extends AbstractReportIntegrationTest {

    private static final String BASE = "/api/haberes/report/ui/cantidadCargos";
    private static final String API_KEY = "test-api-key";
    private static final String HEADER = "X-API-Key";

    @Test
    void devuelveLaCantidadDeCargosDelPeriodo() throws Exception {
        stubNormal();

        MvcResult result = mockMvc.perform(get(BASE + "/data/2026/9").header(HEADER, API_KEY))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        var json = new ObjectMapper().readTree(result.getResponse().getContentAsString());
        assertThat(json.get("anho").asInt()).isEqualTo(2026);
        assertThat(json.get("filas")).hasSize(2);
        var gomez = json.get("filas").get(0);
        assertThat(gomez.get("legajoId").asText()).isEqualTo("1001");
        assertThat(gomez.get("facultad").asText()).isEqualTo("Facultad de Ingeniería");
        assertThat(gomez.get("anuales").asInt()).isEqualTo(1);
        assertThat(gomez.get("bruto").asText()).isEqualTo("1.234.567,89");
        assertThat(json.get("filas").get(1).get("bruto").asText()).isEmpty();
    }

    @Test
    void descargaElPdfDelPeriodo() throws Exception {
        stubNormal();

        MvcResult result = mockMvc.perform(get(BASE + "/pdf/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "cantidadcargos.pdf");
        GoldenFiles.assertPdf("cantidadcargos", "normal", pdf);
    }

    @Test
    void descargaElXlsxDelPeriodo() throws Exception {
        stubNormal();

        MvcResult result = mockMvc.perform(get(BASE + "/xlsx/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] xlsx = ReportHttp.assertAttachment(result, "cantidadcargos.xlsx");
        GoldenFiles.assertWorkbook("cantidadcargos", "normal-xlsx", xlsx);
    }

    @Test
    void descargaElPdfSinDatos() throws Exception {
        stubVacio();

        MvcResult result = mockMvc.perform(get(BASE + "/pdf/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "cantidadcargos.pdf");
        GoldenFiles.assertPdf("cantidadcargos", "vacio", pdf);
    }

    @Test
    void descargaElXlsxSinDatos() throws Exception {
        stubVacio();

        MvcResult result = mockMvc.perform(get(BASE + "/xlsx/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] xlsx = ReportHttp.assertAttachment(result, "cantidadcargos.xlsx");
        GoldenFiles.assertWorkbook("cantidadcargos", "vacio-xlsx", xlsx);
    }

    private void stubNormal() {
        JsonNode fixture = Fixtures.node("cantidadcargos", "normal");
        when(legajoCursoCantidadClient.findAllByPeriodo(2026, 9))
                .thenReturn(Fixtures.list(fixture, "cantidades", LegajoCursoCantidadDto.class));
        when(personaClient.findAllLegajos(anyList()))
                .thenReturn(Fixtures.list(fixture, "personas", PersonaDto.class));
        when(itemClient.findAllByCodigoId(96, 2026, 9))
                .thenReturn(Fixtures.list(fixture, "items", ItemDto.class));
        when(facultadClient.findAll())
                .thenReturn(Fixtures.list(fixture, "facultades", FacultadDto.class));
        when(geograficaClient.findAll())
                .thenReturn(Fixtures.list(fixture, "geograficas", GeograficaDto.class));
    }

    private void stubVacio() {
        JsonNode fixture = Fixtures.node("cantidadcargos", "vacio");
        when(legajoCursoCantidadClient.findAllByPeriodo(2026, 9))
                .thenReturn(Fixtures.list(fixture, "cantidades", LegajoCursoCantidadDto.class));
    }
}
