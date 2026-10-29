package um.haberes.report.reports.docentessede;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/** Namespace del frontend para "Docentes por Sede". */
class DocentesSedeUiTest extends AbstractReportIntegrationTest {

    private static final String BASE = "/api/haberes/report/ui/docentesSede";
    private static final String API_KEY = "test-api-key";
    private static final String HEADER = "X-API-Key";

    @Test
    void devuelve404SiLaFacultadNoExisteEnElCore() throws Exception {
        when(facultadClient.findByFacultadId(99999))
                .thenThrow(feign.FeignException.NotFound.class);

        MvcResult result = mockMvc
                .perform(get(BASE + "/data/99999/3/2026/9").header(HEADER, API_KEY))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(404);
        var json = new ObjectMapper().readTree(result.getResponse().getContentAsString());
        assertThat(json.get("detail").asText()).contains("No existe el registro");
    }

    @Test
    void devuelveLosCursosDeLaSedeConSusDocentes() throws Exception {
        stub(Fixtures.node("docentessede", "normal"));

        MvcResult result = mockMvc
                .perform(get(BASE + "/data/2/3/2026/9").header(HEADER, API_KEY))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        var json = new ObjectMapper().readTree(result.getResponse().getContentAsString());
        assertThat(json.get("facultad").asText()).isEqualTo("Facultad de Ingeniería");
        assertThat(json.get("geografica").asText()).isEqualTo("Sede San Rafael");
        assertThat(json.get("cursos")).hasSize(2);
        var primerCurso = json.get("cursos").get(0);
        assertThat(primerCurso.get("periodo").asText()).isEqualTo("Anual");
        assertThat(primerCurso.get("filas")).hasSize(2);
        assertThat(primerCurso.get("filas").get(0).get("docente").asText()).isEqualTo("Gómez, Ana");
        assertThat(primerCurso.get("filas").get(1).get("designacion").asText()).isEqualTo("Contratado");
    }

    @Test
    void descargaElPdfDelPeriodo() throws Exception {
        stub(Fixtures.node("docentessede", "normal"));

        MvcResult result = mockMvc
                .perform(get(BASE + "/pdf/2/3/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "docentesSede.pdf");
        GoldenFiles.assertPdf("docentessede", "normal", pdf);
    }

    @Test
    void descargaElXlsxDelPeriodo() throws Exception {
        stub(Fixtures.node("docentessede", "normal"));

        MvcResult result = mockMvc
                .perform(get(BASE + "/xlsx/2/3/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] xlsx = ReportHttp.assertAttachment(result, "docentesSede.xlsx");
        GoldenFiles.assertWorkbook("docentessede", "normal-xlsx", xlsx);
    }

    private void stub(JsonNode fixture) {
        when(facultadClient.findByFacultadId(2))
                .thenReturn(Fixtures.one(fixture, "facultad", FacultadDto.class));
        when(geograficaClient.findByGeograficaId(3))
                .thenReturn(Fixtures.one(fixture, "geografica", GeograficaDto.class));
        when(cursoClient.findAllByFacultadIdAndGeograficaIdAndAnhoAndMes(2, 3, 2026, 9))
                .thenReturn(Fixtures.list(fixture, "cursos", CursoDto.class));

        Map<Long, List<CursoCargoDto>> cargosPorCurso =
                Fixtures.list(fixture, "cursoCargos", CursoCargoDto.class).stream()
                        .collect(Collectors.groupingBy(CursoCargoDto::getCursoId));
        cargosPorCurso.forEach((cursoId, cargos) ->
                when(cursoCargoClient.findAllByCurso(cursoId, 2026, 9)).thenReturn(cargos));

        Map<Long, List<CursoCargoContratadoDto>> contratadosPorCurso =
                Fixtures.list(fixture, "contratados", CursoCargoContratadoDto.class).stream()
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
