package um.haberes.report.reports.historicoasignacioncargos;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CategoriaDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.DependenciaDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.FacultadDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.GeograficaDto;
import um.haberes.report.support.AbstractReportIntegrationTest;
import um.haberes.report.support.Fixtures;
import um.haberes.report.support.GoldenFiles;
import um.haberes.report.support.ReportHttp;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Reporte "Historico de Asignacion de Cargos"
 * ({@code GET /historico/historicoAsignacionDeCargos/{categoriaId}}).
 *
 * <p>Contrato: PDF attachment {@code historicoAsignacionDeCargos.pdf}. Contenido legacy: solo
 * el encabezado (categoria + titulo). El cuerpo del recorrido por categoria esta vacio en el
 * codigo de produccion, pero las cuatro listas se siguen consultando al core.
 */
class HistoricoAsignacionCargosReportTest extends AbstractReportIntegrationTest {

    private static final String URL = "/api/haberes/report/historico/historicoAsignacionDeCargos/5";

    @Test
    void generaPdfSoloConEncabezado() throws Exception {
        JsonNode fixture = Fixtures.node("historicoasignacioncargos", "normal");
        stub(fixture);

        MvcResult result = mockMvc.perform(get(URL)).andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "historicoAsignacionDeCargos.pdf");
        GoldenFiles.assertPdf("historicoasignacioncargos", "normal", pdf);
    }

    /** Con todas las listas vacias el PDF es identico al caso con datos: solo encabezado. */
    @Test
    void generaPdfValidoSinDatos() throws Exception {
        JsonNode fixture = Fixtures.node("historicoasignacioncargos", "vacio");
        stub(fixture);

        MvcResult result = mockMvc.perform(get(URL)).andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "historicoAsignacionDeCargos.pdf");
        GoldenFiles.assertPdf("historicoasignacioncargos", "vacio", pdf);
    }

    private void stub(JsonNode fixture) {
        when(geograficaClient.findAll())
                .thenReturn(Fixtures.list(fixture, "geograficas", GeograficaDto.class));
        when(categoriaClient.findAll())
                .thenReturn(Fixtures.list(fixture, "categorias", CategoriaDto.class));
        when(dependenciaClient.findAll())
                .thenReturn(Fixtures.list(fixture, "dependencias", DependenciaDto.class));
        when(facultadClient.findAll())
                .thenReturn(Fixtures.list(fixture, "facultades", FacultadDto.class));
    }
}
