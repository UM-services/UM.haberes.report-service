package um.haberes.report.reports.designacionesdocente;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.DocenteDesignacionDto;
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

/** Namespace del frontend para "Designación de Docentes". */
class DesignacionesDocenteUiTest extends AbstractReportIntegrationTest {

    private static final String BASE = "/api/haberes/report/ui/designacionesDocente";
    private static final String API_KEY = "test-api-key";
    private static final String HEADER = "X-API-Key";

    @Test
    void devuelveDesignacionesAgrupadasPorDocente() throws Exception {
        stub(Fixtures.node("designacionesdocente", "normal"));

        MvcResult result = mockMvc.perform(get(BASE + "/data/2026/9").header(HEADER, API_KEY))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        var json = new ObjectMapper().readTree(result.getResponse().getContentAsString());
        assertThat(json.get("docentes")).hasSize(2);
        var gomez = json.get("docentes").get(0);
        assertThat(gomez.get("legajoId").asLong()).isEqualTo(1001);
        assertThat(gomez.get("apellidoNombre").asText()).isEqualTo("Gómez, Ana");
        assertThat(gomez.get("filas")).hasSize(2);
        assertThat(gomez.get("filas").get(0).get("dictado").asText()).isEqualTo("Anual");
        assertThat(gomez.get("filas").get(0).get("horasDesignacion").asText()).isEqualTo("10.0");
    }

    @Test
    void descargaElPdfDelPeriodo() throws Exception {
        stub(Fixtures.node("designacionesdocente", "normal"));

        MvcResult result = mockMvc.perform(get(BASE + "/pdf/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "designaciones-docente.pdf");
        GoldenFiles.assertPdf("designacionesdocente", "normal", pdf);
    }

    @Test
    void descargaElXlsxDelPeriodo() throws Exception {
        stub(Fixtures.node("designacionesdocente", "normal"));

        MvcResult result = mockMvc.perform(get(BASE + "/xlsx/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] xlsx = ReportHttp.assertAttachment(result, "designaciones-docente.xlsx");
        GoldenFiles.assertWorkbook("designacionesdocente", "normal-xlsx", xlsx);
    }

    private void stub(JsonNode fixture) {
        when(personaClient.findAllDocente(2026, 9))
                .thenReturn(Fixtures.list(fixture, "personas", PersonaDto.class));
        when(docenteDesignacionClient.findAllByPeriodo(2026, 9))
                .thenReturn(Fixtures.list(fixture, "designaciones", DocenteDesignacionDto.class));
        when(facultadClient.findAllFacultades())
                .thenReturn(Fixtures.list(fixture, "facultades", FacultadDto.class));
        when(geograficaClient.findAllByGeograficaIdIn(anyList()))
                .thenReturn(Fixtures.list(fixture, "geograficas", GeograficaDto.class));
    }
}
