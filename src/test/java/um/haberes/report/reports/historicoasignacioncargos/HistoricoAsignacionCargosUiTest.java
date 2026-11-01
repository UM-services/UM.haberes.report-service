package um.haberes.report.reports.historicoasignacioncargos;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import um.haberes.report.support.AbstractReportIntegrationTest;
import um.haberes.report.support.GoldenFiles;
import um.haberes.report.support.ReportHttp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/** Namespace del frontend para el "Histórico de Asignación de Cargos". */
class HistoricoAsignacionCargosUiTest extends AbstractReportIntegrationTest {

    private static final String BASE = "/api/haberes/report/ui/historicoCargos";
    private static final String API_KEY = "test-api-key";
    private static final String HEADER = "X-API-Key";

    @Test
    void devuelveElAvisoDeCuerpoNoImplementado() throws Exception {
        MvcResult result = mockMvc.perform(get(BASE + "/data/5").header(HEADER, API_KEY))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        var json = new ObjectMapper().readTree(result.getResponse().getContentAsString());
        assertThat(json.get("categoriaId").asText()).isEqualTo("5");
        assertThat(json.get("aviso").asText()).contains("no tiene cuerpo implementado");
    }

    /** El PDF del namespace es el mismo del reporte legacy: se reutiliza su golden. */
    @Test
    void descargaElPdfDeLaCategoria() throws Exception {
        MvcResult result = mockMvc.perform(get(BASE + "/pdf/5").header(HEADER, API_KEY))
                .andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "historicoAsignacionDeCargos.pdf");
        GoldenFiles.assertPdf("historicoasignacioncargos", "normal", pdf);
    }
}
