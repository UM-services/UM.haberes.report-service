package um.haberes.report.reports.docentessede;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CargoTipoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CursoCargoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CursoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.FacultadDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.GeograficaDto;
import um.haberes.report.shared.infrastructure.client.tesoreria.core.dto.CursoCargoContratadoDto;
import um.haberes.report.shared.infrastructure.client.tesoreria.core.dto.PersonaDto;
import um.haberes.report.support.AbstractReportIntegrationTest;
import um.haberes.report.support.Fixtures;
import um.haberes.report.support.GoldenFiles;
import um.haberes.report.support.ReportHttp;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Reporte "Docentes por Sede"
 * ({@code GET /docentes/docentesSede/{facultadId}/{geograficaId}/{anho}/{mes}}).
 *
 * <p>Contrato: PDF attachment {@code docentesSede.pdf}. Contenido: un bloque por curso de la
 * sede, con una fila por cargo docente y otra por cargo contratado (resuelto contra
 * tesoreria-core {@code persona/unique}).
 */
class DocentesSedeReportTest extends AbstractReportIntegrationTest {

    private static final String URL = "/api/haberes/report/docentes/docentesSede/2/3/2026/9";

    @Test
    void generaPdfConCursosDocentesYContratados() throws Exception {
        JsonNode fixture = Fixtures.node("docentessede", "normal");
        stub(fixture);

        MvcResult result = mockMvc.perform(get(URL)).andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "docentesSede.pdf");
        GoldenFiles.assertPdf("docentessede", "normal", pdf);
    }

    /** Sin cursos el service igual cierra el documento: PDF valido con solo el encabezado. */
    @Test
    void generaPdfValidoSinCursos() throws Exception {
        JsonNode fixture = Fixtures.node("docentessede", "vacio");
        stub(fixture);

        MvcResult result = mockMvc.perform(get(URL)).andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "docentesSede.pdf");
        GoldenFiles.assertPdf("docentessede", "vacio", pdf);
    }

    private void stub(JsonNode fixture) {
        when(facultadClient.findByFacultadId(2))
                .thenReturn(Fixtures.one(fixture, "facultad", FacultadDto.class));
        when(geograficaClient.findByGeograficaId(3))
                .thenReturn(Fixtures.one(fixture, "geografica", GeograficaDto.class));
        when(cursoClient.findAllByFacultadIdAndGeograficaIdAndAnhoAndMes(2, 3, 2026, 9))
                .thenReturn(Fixtures.list(fixture, "cursos", CursoDto.class));

        Map<Long, List<CursoCargoDto>> cargosPorCurso = Fixtures.list(fixture, "cursoCargos", CursoCargoDto.class)
                .stream()
                .collect(Collectors.groupingBy(CursoCargoDto::getCursoId));
        cargosPorCurso.forEach((cursoId, cargos) ->
                when(cursoCargoClient.findAllByCurso(cursoId, 2026, 9)).thenReturn(cargos));

        Map<Long, List<CursoCargoContratadoDto>> contratadosPorCurso =
                Fixtures.list(fixture, "contratados", CursoCargoContratadoDto.class)
                        .stream()
                        .collect(Collectors.groupingBy(CursoCargoContratadoDto::getCursoId));
        contratadosPorCurso.forEach((cursoId, contratados) ->
                when(cursoCargoContratadoClient.findAllByCurso(cursoId, 2026, 9)).thenReturn(contratados));

        for (CargoTipoDto cargoTipo : Fixtures.list(fixture, "cargoTipos", CargoTipoDto.class)) {
            when(cargoTipoClient.findByCargoTipoId(cargoTipo.getCargoTipoId())).thenReturn(cargoTipo);
        }

        for (PersonaDto persona : Fixtures.list(fixture, "personasTesoreria", PersonaDto.class)) {
            when(personaTesoreriaClient.findByUnique(persona.getPersonaId(), persona.getDocumentoId()))
                    .thenReturn(persona);
        }
    }
}
