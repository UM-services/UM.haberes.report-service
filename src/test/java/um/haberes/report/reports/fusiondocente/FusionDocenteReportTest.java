package um.haberes.report.reports.fusiondocente;

import com.fasterxml.jackson.databind.JsonNode;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Reporte "Fusion por Docente"
 * ({@code GET /fusionDocente/fusionDocente/{anho}/{mes}}).
 *
 * <p>Contrato: PDF attachment {@code fusion.docente.pdf}. Contenido: una fila por curso
 * fusionado del docente, con facultad, sede, categoria y basico formateado.
 */
class FusionDocenteReportTest extends AbstractReportIntegrationTest {

    private static final String URL = "/api/haberes/report/fusionDocente/fusionDocente/2026/9";

    @Test
    void generaPdfConFusionesDeDocentes() throws Exception {
        JsonNode fixture = Fixtures.node("fusiondocente", "normal");
        stub(fixture);

        MvcResult result = mockMvc.perform(get(URL)).andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "fusion.docente.pdf");
        GoldenFiles.assertPdf("fusiondocente", "normal", pdf);
    }

    /** Sin categorias se emite un PDF valido con el encabezado y la leyenda C1. */
    @Test
    void generaPdfValidoSinCategorias() throws Exception {
        JsonNode fixture = Fixtures.node("fusiondocente", "vacio");
        stub(fixture);

        MvcResult result = mockMvc.perform(get(URL)).andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "fusion.docente.pdf");
        GoldenFiles.assertPdf("fusiondocente", "vacio", pdf);
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
        // Una sola consulta al período (antes era una por legajo).
        when(cursoFusionClient.findAllByPeriodo(2026, 9))
                .thenReturn(Fixtures.list(fixture, "fusiones", CursoFusionDto.class));
    }
}
