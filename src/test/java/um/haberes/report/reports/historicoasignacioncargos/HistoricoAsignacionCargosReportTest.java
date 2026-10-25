package um.haberes.report.reports.historicoasignacioncargos;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import um.haberes.report.support.AbstractReportIntegrationTest;
import um.haberes.report.support.GoldenFiles;
import um.haberes.report.support.ReportHttp;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Reporte "Historico de Asignacion de Cargos"
 * ({@code GET /historico/historicoAsignacionDeCargos/{categoriaId}}).
 *
 * <p>Contrato: PDF attachment {@code historicoAsignacionDeCargos.pdf}. Contenido: sólo el
 * encabezado con la categoría. El cuerpo del recorrido por categoría nunca se implementó en el
 * sistema previo; se conserva el mismo PDF y no se inventan filas.
 */
class HistoricoAsignacionCargosReportTest extends AbstractReportIntegrationTest {

    private static final String URL = "/api/haberes/report/historico/historicoAsignacionDeCargos/5";

    @Test
    void generaPdfSoloConEncabezado() throws Exception {
        MvcResult result = mockMvc.perform(get(URL)).andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "historicoAsignacionDeCargos.pdf");
        GoldenFiles.assertPdf("historicoasignacioncargos", "normal", pdf);
    }

    /** El resultado no depende de catálogos: con o sin datos, el PDF es el mismo encabezado. */
    @Test
    void generaPdfValidoSinDatos() throws Exception {
        MvcResult result = mockMvc.perform(get(URL)).andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "historicoAsignacionDeCargos.pdf");
        GoldenFiles.assertPdf("historicoasignacioncargos", "vacio", pdf);
    }
}
