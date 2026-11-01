package um.haberes.report.reports.sheet;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CargoTipoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CursoCargoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CursoDto;
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

/** Namespace del frontend para la "Planilla de Cursos por Período" (slice {@code sheet}). */
class SheetCursosUiTest extends AbstractReportIntegrationTest {

    private static final String BASE = "/api/haberes/report/ui/cursosPeriodo";
    private static final String API_KEY = "test-api-key";
    private static final String HEADER = "X-API-Key";

    @Test
    void devuelveLosCursosDelPeriodo() throws Exception {
        stub(Fixtures.node("sheet", "normal"));

        MvcResult result = mockMvc.perform(get(BASE + "/data/2026/9").header(HEADER, API_KEY))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        var json = new ObjectMapper().readTree(result.getResponse().getContentAsString());
        assertThat(json.get("filas")).hasSize(3);
        var gomez = json.get("filas").get(0);
        assertThat(gomez.get("legajoId").asText()).isEqualTo("1001");
        assertThat(gomez.get("curso").asText()).isEqualTo("Análisis Matemático I");
        assertThat(gomez.get("horas").asText()).isEqualTo("10.5");
        assertThat(gomez.get("dictado").asText()).isEqualTo("Anual");
    }

    @Test
    void descargaElPdfDelPeriodo() throws Exception {
        stub(Fixtures.node("sheet", "normal"));

        MvcResult result = mockMvc.perform(get(BASE + "/pdf/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "cursos.pdf");
        GoldenFiles.assertPdf("cursosperiodo", "normal", pdf);
    }

    /** El XLSX del namespace es el mismo archivo del reporte legacy: se reutiliza su golden. */
    @Test
    void descargaElXlsxDelPeriodo() throws Exception {
        stub(Fixtures.node("sheet", "normal"));

        MvcResult result = mockMvc.perform(get(BASE + "/xlsx/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] xlsx = ReportHttp.assertAttachment(result, "cursos.xlsx");
        GoldenFiles.assertWorkbook("sheet", "normal", xlsx);
    }

    @Test
    void descargaElPdfSinDatos() throws Exception {
        stub(Fixtures.node("sheet", "vacio"));

        MvcResult result = mockMvc.perform(get(BASE + "/pdf/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "cursos.pdf");
        GoldenFiles.assertPdf("cursosperiodo", "vacio", pdf);
    }

    @Test
    void descargaElXlsxSinDatos() throws Exception {
        stub(Fixtures.node("sheet", "vacio"));

        MvcResult result = mockMvc.perform(get(BASE + "/xlsx/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] xlsx = ReportHttp.assertAttachment(result, "cursos.xlsx");
        GoldenFiles.assertWorkbook("sheet", "vacio", xlsx);
    }

    private void stub(JsonNode fixture) {
        when(facultadClient.findAll())
                .thenReturn(Fixtures.list(fixture, "facultades", FacultadDto.class));
        when(geograficaClient.findAll())
                .thenReturn(Fixtures.list(fixture, "geograficas", GeograficaDto.class));
        when(cursoClient.findAll())
                .thenReturn(Fixtures.list(fixture, "cursos", CursoDto.class));
        when(cargoTipoClient.findAll())
                .thenReturn(Fixtures.list(fixture, "cargoTipos", CargoTipoDto.class));
        when(personaClient.findAllLegajos(anyList()))
                .thenReturn(Fixtures.list(fixture, "personas", PersonaDto.class));
        when(cursoCargoClient.findAllByPeriodo(2026, 9))
                .thenReturn(Fixtures.list(fixture, "cursoCargos", CursoCargoDto.class));
    }
}
