package um.haberes.report.reports.cursodocente;

import com.fasterxml.jackson.databind.JsonNode;
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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Reporte "Cursos por Docente" ({@code GET /curso-docente/pdf/{anho}/{mes}}).
 *
 * <p>Contrato: PDF attachment {@code curso-docente.pdf}. Contenido: un bloque por docente
 * (ordenado por apellido) que solo aparece si tiene cursos cargo en el periodo, y una fila por
 * curso cargo.
 */
class CursoDocenteReportTest extends AbstractReportIntegrationTest {

    private static final String URL = "/api/haberes/report/curso-docente/pdf/2026/9";

    @Test
    void generaPdfConDocentesYCursos() throws Exception {
        JsonNode fixture = Fixtures.node("cursodocente", "normal");
        stub(fixture);

        MvcResult result = mockMvc.perform(get(URL)).andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "curso-docente.pdf");
        GoldenFiles.assertPdf("cursodocente", "normal", pdf);
    }

    /** Sin personas se emite un PDF valido con el encabezado y la leyenda C1. */
    @Test
    void generaPdfConLeyendaSinPersonas() throws Exception {
        JsonNode fixture = Fixtures.node("cursodocente", "vacio");
        stub(fixture);

        MvcResult result = mockMvc.perform(get(URL)).andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "curso-docente.pdf");
        GoldenFiles.assertPdf("cursodocente", "vacio", pdf);
    }

    private void stub(JsonNode fixture) {
        when(personaClient.findAll())
                .thenReturn(Fixtures.list(fixture, "personas", PersonaDto.class));
        when(cursoClient.findAll())
                .thenReturn(Fixtures.list(fixture, "cursos", CursoDto.class));
        when(categoriaClient.findAll())
                .thenReturn(Fixtures.list(fixture, "categorias", CategoriaDto.class));
        when(facultadClient.findAllFacultades())
                .thenReturn(Fixtures.list(fixture, "facultades", FacultadDto.class));
        // El service arma los ids desde un Set, por lo que el orden del argumento no es estable:
        // se matchea cualquier lista y el fixture define el contenido devuelto.
        when(geograficaClient.findAllByGeograficaIdIn(anyList()))
                .thenReturn(Fixtures.list(fixture, "geograficas", GeograficaDto.class));

        Map<Long, List<CursoCargoDto>> cursosPorLegajo = Fixtures.list(fixture, "cursosCargo", CursoCargoDto.class)
                .stream()
                .collect(Collectors.groupingBy(CursoCargoDto::getLegajoId));
        cursosPorLegajo.forEach((legajoId, cursosCargo) ->
                when(cursoCargoClient.findAllByLegajo(legajoId, 2026, 9)).thenReturn(cursosCargo));
    }
}
