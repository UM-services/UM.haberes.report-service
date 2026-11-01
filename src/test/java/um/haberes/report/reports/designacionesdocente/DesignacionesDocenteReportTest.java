package um.haberes.report.reports.designacionesdocente;

import com.fasterxml.jackson.databind.JsonNode;
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

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Reporte "Designaciones por Docentes"
 * ({@code GET /designacionesDocente/pdf/{anho}/{mes}}).
 *
 * <p>Contrato: PDF attachment {@code designaciones-docente.pdf}. Contenido: un bloque por
 * docente con designaciones (ordenado por apellido), con una fila por designacion ordenada por
 * espacio y la columna "Dictado" derivada de los flags anual/semestre1/semestre2.
 */
class DesignacionesDocenteReportTest extends AbstractReportIntegrationTest {

    private static final String URL = "/api/haberes/report/designacionesDocente/pdf/2026/9";

    @Test
    void generaPdfConDesignacionesPorDocente() throws Exception {
        JsonNode fixture = Fixtures.node("designacionesdocente", "normal");
        stub(fixture);

        MvcResult result = mockMvc.perform(get(URL)).andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "designaciones-docente.pdf");
        GoldenFiles.assertPdf("designacionesdocente", "normal", pdf);
    }

    /** Sin docentes se emite un PDF valido con el encabezado y la leyenda C1. */
    @Test
    void generaPdfConLeyendaSinDocentes() throws Exception {
        JsonNode fixture = Fixtures.node("designacionesdocente", "vacio");
        stub(fixture);

        MvcResult result = mockMvc.perform(get(URL)).andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "designaciones-docente.pdf");
        GoldenFiles.assertPdf("designacionesdocente", "vacio", pdf);
    }

    private void stub(JsonNode fixture) {
        when(personaClient.findAllDocente(2026, 9))
                .thenReturn(Fixtures.list(fixture, "personas", PersonaDto.class));
        when(docenteDesignacionClient.findAllByPeriodo(2026, 9))
                .thenReturn(Fixtures.list(fixture, "designaciones", DocenteDesignacionDto.class));
        when(facultadClient.findAllFacultades())
                .thenReturn(Fixtures.list(fixture, "facultades", FacultadDto.class));
        // El service arma los ids desde un Set, por lo que el orden del argumento no es estable.
        when(geograficaClient.findAllByGeograficaIdIn(anyList()))
                .thenReturn(Fixtures.list(fixture, "geograficas", GeograficaDto.class));
    }
}
