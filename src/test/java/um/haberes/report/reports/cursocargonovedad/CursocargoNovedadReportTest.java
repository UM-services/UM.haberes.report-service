package um.haberes.report.reports.cursocargonovedad;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CursoCargoNovedadDto;
import um.haberes.report.support.AbstractReportIntegrationTest;
import um.haberes.report.support.Fixtures;
import um.haberes.report.support.GoldenFiles;
import um.haberes.report.support.ReportHttp;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Reporte "Novedades de Cursos" (padrón del período y sólo pendientes).
 *
 * <p>Contrato: PDF inline. Contenido: una fila por novedad con facultad, sede, legajo, docente,
 * curso, cargo, horas, desarraigo, tipo (Alta / Baja / Cambio / Alta + cambio), estado
 * (Autorizada / Rechazada / Pendiente) y solicitud.
 */
class CursocargoNovedadReportTest extends AbstractReportIntegrationTest {

    private static final String PERIODO_URL = "/api/haberes/report/cursocargonovedad/periodo/2026/9";
    private static final String PENDIENTES_URL = "/api/haberes/report/cursocargonovedad/pendientes/2026/9";

    @Test
    void generaPdfDelPeriodoConTodasLasNovedades() throws Exception {
        JsonNode fixture = Fixtures.node("cursocargonovedad", "normal");
        when(cursoCargoNovedadClient.findAllByPeriodo(2026, 9))
                .thenReturn(Fixtures.list(fixture, "novedades", CursoCargoNovedadDto.class));

        MvcResult result = mockMvc.perform(get(PERIODO_URL)).andReturn();

        byte[] pdf = ReportHttp.assertInlinePdf(result, "novedades-cursos-9-2026.pdf");
        GoldenFiles.assertPdf("cursocargonovedad", "periodo-normal", pdf);
    }

    @Test
    void generaPdfDelPeriodoVacio() throws Exception {
        JsonNode fixture = Fixtures.node("cursocargonovedad", "vacio");
        when(cursoCargoNovedadClient.findAllByPeriodo(2026, 9))
                .thenReturn(Fixtures.list(fixture, "novedades", CursoCargoNovedadDto.class));

        MvcResult result = mockMvc.perform(get(PERIODO_URL)).andReturn();

        byte[] pdf = ReportHttp.assertInlinePdf(result, "novedades-cursos-9-2026.pdf");
        GoldenFiles.assertPdf("cursocargonovedad", "periodo-vacio", pdf);
    }

    @Test
    void generaPdfSoloPendientes() throws Exception {
        JsonNode fixture = Fixtures.node("cursocargonovedad", "normal");
        when(cursoCargoNovedadClient.findAllPendientes(2026, 9))
                .thenReturn(Fixtures.list(fixture, "pendientes", CursoCargoNovedadDto.class));

        MvcResult result = mockMvc.perform(get(PENDIENTES_URL)).andReturn();

        byte[] pdf = ReportHttp.assertInlinePdf(result, "novedades-cursos-pendientes-9-2026.pdf");
        GoldenFiles.assertPdf("cursocargonovedad", "pendientes-normal", pdf);
    }

    @Test
    void generaPdfSoloPendientesVacio() throws Exception {
        JsonNode fixture = Fixtures.node("cursocargonovedad", "vacio");
        when(cursoCargoNovedadClient.findAllPendientes(2026, 9))
                .thenReturn(Fixtures.list(fixture, "pendientes", CursoCargoNovedadDto.class));

        MvcResult result = mockMvc.perform(get(PENDIENTES_URL)).andReturn();

        byte[] pdf = ReportHttp.assertInlinePdf(result, "novedades-cursos-pendientes-9-2026.pdf");
        GoldenFiles.assertPdf("cursocargonovedad", "pendientes-vacio", pdf);
    }
}
