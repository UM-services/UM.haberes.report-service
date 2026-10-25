package um.haberes.report.reports.fusiondocente;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CategoriaDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CursoFusionDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.FacultadDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.GeograficaDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.PersonaDto;
import um.haberes.report.support.AbstractReportIntegrationTest;
import um.haberes.report.support.Fixtures;
import um.haberes.report.support.GoldenFiles;
import um.haberes.report.support.ReportHttp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/** Namespace del frontend para "Fusión por Docente". */
class FusionDocenteUiTest extends AbstractReportIntegrationTest {

    private static final String BASE = "/api/haberes/report/ui/fusionDocente";
    private static final String API_KEY = "test-api-key";
    private static final String HEADER = "X-API-Key";

    @Test
    void devuelveLasFusionesPorDocente() throws Exception {
        stub(Fixtures.node("fusiondocente", "normal"));

        MvcResult result = mockMvc.perform(get(BASE + "/data/2026/9").header(HEADER, API_KEY))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        var json = new ObjectMapper().readTree(result.getResponse().getContentAsString());
        assertThat(json.get("filas")).hasSize(2);
        var gomez = json.get("filas").get(0);
        assertThat(gomez.get("legajoId").asText()).isEqualTo("1001");
        assertThat(gomez.get("apellidoNombre").asText()).isEqualTo("Gómez, Ana");
        assertThat(gomez.get("basico").asText()).isEqualTo("1.234.567,89");
    }

    @Test
    void descargaElPdfDelPeriodo() throws Exception {
        stub(Fixtures.node("fusiondocente", "normal"));

        MvcResult result = mockMvc.perform(get(BASE + "/pdf/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "fusion.docente.pdf");
        GoldenFiles.assertPdf("fusiondocente", "normal", pdf);
    }

    @Test
    void descargaElXlsxDelPeriodo() throws Exception {
        stub(Fixtures.node("fusiondocente", "normal"));

        MvcResult result = mockMvc.perform(get(BASE + "/xlsx/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] xlsx = ReportHttp.assertAttachment(result, "fusion.docente.xlsx");
        GoldenFiles.assertWorkbook("fusiondocente", "normal-xlsx", xlsx);
    }

    private void stub(JsonNode fixture) {
        when(categoriaClient.findAll())
                .thenReturn(Fixtures.list(fixture, "categorias", CategoriaDto.class));
        when(personaClient.findAllLegajos(anyList()))
                .thenReturn(Fixtures.list(fixture, "personas", PersonaDto.class));
        when(facultadClient.findAll())
                .thenReturn(Fixtures.list(fixture, "facultades", FacultadDto.class));
        when(geograficaClient.findAll())
                .thenReturn(Fixtures.list(fixture, "geograficas", GeograficaDto.class));
        when(cursoFusionClient.findAllByPeriodo(2026, 9))
                .thenReturn(Fixtures.list(fixture, "fusiones", CursoFusionDto.class));
    }
}
