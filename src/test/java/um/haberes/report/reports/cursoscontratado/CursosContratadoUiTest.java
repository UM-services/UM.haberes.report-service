package um.haberes.report.reports.cursoscontratado;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CargoTipoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.ContratadoPersonaDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CursoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.FacultadDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.GeograficaDto;
import um.haberes.report.shared.infrastructure.client.tesoreria.core.dto.CursoCargoContratadoDto;
import um.haberes.report.support.AbstractReportIntegrationTest;
import um.haberes.report.support.Fixtures;
import um.haberes.report.support.GoldenFiles;
import um.haberes.report.support.ReportHttp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/** Namespace del frontend para la "Planilla de Cursos por Contratado". */
class CursosContratadoUiTest extends AbstractReportIntegrationTest {

    private static final String BASE = "/api/haberes/report/ui/cursosContratado";
    private static final String API_KEY = "test-api-key";
    private static final String HEADER = "X-API-Key";

    @Test
    void devuelveLosCursosPorContratado() throws Exception {
        stubNormal();

        MvcResult result = mockMvc.perform(get(BASE + "/data/2026/9").header(HEADER, API_KEY))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        var json = new ObjectMapper().readTree(result.getResponse().getContentAsString());
        assertThat(json.get("filas")).hasSize(2);
        var sosa = json.get("filas").get(0);
        assertThat(sosa.get("cuit").asText()).isEqualTo("20-11111111-3");
        assertThat(sosa.get("apellidoNombre").asText()).isEqualTo("Sosa, Luis");
        assertThat(sosa.get("periodo").asText()).isEqualTo("9/2026");
        assertThat(sosa.get("curso").asText()).isEqualTo("Análisis Matemático I");
        assertThat(sosa.get("horas").asText()).isEqualTo("6.5");
        assertThat(sosa.get("dictado").asText()).isEqualTo("Anual");
        assertThat(json.get("filas").get(1).get("dictado").asText()).isEqualTo("2do Semestre");
    }

    @Test
    void descargaElPdfDelPeriodo() throws Exception {
        stubNormal();

        MvcResult result = mockMvc.perform(get(BASE + "/pdf/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "cursoscontratado.pdf");
        GoldenFiles.assertPdf("cursoscontratado", "normal", pdf);
    }

    @Test
    void descargaElXlsxDelPeriodo() throws Exception {
        stubNormal();

        MvcResult result = mockMvc.perform(get(BASE + "/xlsx/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] xlsx = ReportHttp.assertAttachment(result, "cursoscontratado.xlsx");
        GoldenFiles.assertWorkbook("cursoscontratado", "normal-xlsx", xlsx);
    }

    @Test
    void descargaElPdfSinDatos() throws Exception {
        JsonNode fixture = Fixtures.node("cursoscontratado", "vacio");
        when(contratadoPersonaClient.findAll())
                .thenReturn(Fixtures.list(fixture, "contratados", ContratadoPersonaDto.class));

        MvcResult result = mockMvc.perform(get(BASE + "/pdf/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "cursoscontratado.pdf");
        GoldenFiles.assertPdf("cursoscontratado", "vacio", pdf);
    }

    private void stubNormal() {
        JsonNode fixture = Fixtures.node("cursoscontratado", "normal");
        when(contratadoPersonaClient.findAll())
                .thenReturn(Fixtures.list(fixture, "contratados", ContratadoPersonaDto.class));
        when(cursoCargoContratadoClient.findAllByPeriodo(2026, 9))
                .thenReturn(Fixtures.list(fixture, "cursosContratados", CursoCargoContratadoDto.class));
        when(cursoClient.findAll())
                .thenReturn(Fixtures.list(fixture, "cursos", CursoDto.class));
        when(facultadClient.findAll())
                .thenReturn(Fixtures.list(fixture, "facultades", FacultadDto.class));
        when(geograficaClient.findAll())
                .thenReturn(Fixtures.list(fixture, "geograficas", GeograficaDto.class));
        when(cargoTipoClient.findAll())
                .thenReturn(Fixtures.list(fixture, "cargoTipos", CargoTipoDto.class));
    }
}
