package um.haberes.report.reports.cursodocente;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CategoriaDto;
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

/** Namespace del frontend para "Cursos por Docente". */
class CursoDocenteUiTest extends AbstractReportIntegrationTest {

    private static final String BASE = "/api/haberes/report/ui/cursoDocente";
    private static final String API_KEY = "test-api-key";
    private static final String HEADER = "X-API-Key";

    @Test
    void devuelveLosCursosAgrupadosPorDocente() throws Exception {
        stub(Fixtures.node("cursodocente", "normal"));

        MvcResult result = mockMvc.perform(get(BASE + "/data/2026/9").header(HEADER, API_KEY))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        var json = new ObjectMapper().readTree(result.getResponse().getContentAsString());
        assertThat(json.get("docentes")).hasSize(2);
        var gomez = json.get("docentes").get(0);
        assertThat(gomez.get("legajoId").asLong()).isEqualTo(1001);
        assertThat(gomez.get("filas")).hasSize(2);
        assertThat(gomez.get("filas").get(0).get("espacio").asText())
                .isEqualTo("Análisis Matemático I");
        assertThat(gomez.get("filas").get(0).get("basico").asText()).isEqualTo("1234567.89");
        assertThat(gomez.get("filas").get(0).get("anual").asText()).isEqualTo("1");
    }

    @Test
    void descargaElPdfDelPeriodo() throws Exception {
        stub(Fixtures.node("cursodocente", "normal"));

        MvcResult result = mockMvc.perform(get(BASE + "/pdf/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "curso-docente.pdf");
        GoldenFiles.assertPdf("cursodocente", "normal", pdf);
    }

    @Test
    void descargaElXlsxDelPeriodo() throws Exception {
        stub(Fixtures.node("cursodocente", "normal"));

        MvcResult result = mockMvc.perform(get(BASE + "/xlsx/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] xlsx = ReportHttp.assertAttachment(result, "curso-docente.xlsx");
        GoldenFiles.assertWorkbook("cursodocente", "normal-xlsx", xlsx);
    }

    private void stub(JsonNode fixture) {
        when(personaClient.findAllLegajos(anyList()))
                .thenReturn(Fixtures.list(fixture, "personas", PersonaDto.class));
        when(cursoClient.findAll())
                .thenReturn(Fixtures.list(fixture, "cursos", CursoDto.class));
        when(categoriaClient.findAll())
                .thenReturn(Fixtures.list(fixture, "categorias", CategoriaDto.class));
        when(facultadClient.findAllFacultades())
                .thenReturn(Fixtures.list(fixture, "facultades", FacultadDto.class));
        when(geograficaClient.findAllByGeograficaIdIn(anyList()))
                .thenReturn(Fixtures.list(fixture, "geograficas", GeograficaDto.class));
        when(cursoCargoClient.findAllByPeriodo(2026, 9))
                .thenReturn(Fixtures.list(fixture, "cursosCargo", CursoCargoDto.class));
    }
}
