package um.haberes.report.reports.sheet;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CargoTipoDto;
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

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Hoja Excel de cursos y cargos docentes
 * ({@code GET /api/haberes/report/sheet/generatecursos/{anho}/{mes}}).
 *
 * <p>Contrato: XLSX attachment {@code cursos.xlsx}. Contenido: una fila por curso-cargo de cada
 * legajo del periodo, con una unica hoja "Cursos" y las columnas legajo, apellido y nombre,
 * periodo, facultad, sede, curso, cargo, horas y dictado.
 *
 * <p>El service resuelve todos los catalogos en memoria (facultades, sedes, cursos y tipos de
 * cargo) y recorre los cursos del docente, por lo que un docente con dos cursos produce dos
 * filas. La columna "Dictado" se decide con ifs encadenados: si un curso tuviera mas de un flag
 * activo gana el ultimo evaluado (2do Semestre sobre 1er Semestre sobre Anual).
 *
 * <p>Sin datos en el periodo el archivo se emite igual, con la fila de encabezados unicamente.
 */
class SheetReportTest extends AbstractReportIntegrationTest {

    private static final String URL = "/api/haberes/report/sheet/generatecursos/2026/9";

    @Test
    void generaExcelConCursosPorDocente() throws Exception {
        JsonNode fixture = Fixtures.node("sheet", "normal");
        stub(fixture);

        MvcResult result = mockMvc.perform(get(URL)).andReturn();

        byte[] xlsx = ReportHttp.assertAttachment(result, "cursos.xlsx");
        GoldenFiles.assertWorkbook("sheet", "normal", xlsx);
    }

    @Test
    void generaExcelConEncabezadoSinCursos() throws Exception {
        JsonNode fixture = Fixtures.node("sheet", "vacio");
        stub(fixture);

        MvcResult result = mockMvc.perform(get(URL)).andReturn();

        byte[] xlsx = ReportHttp.assertAttachment(result, "cursos.xlsx");
        GoldenFiles.assertWorkbook("sheet", "vacio", xlsx);
    }

    private void stub(JsonNode fixture) {
        when(facultadClient.findAll())
                .thenReturn(Fixtures.list(fixture, "facultades", FacultadDto.class));
        when(geograficaClient.findAll())
                .thenReturn(Fixtures.list(fixture, "geograficas", GeograficaDto.class));
        when(cursoClient.findAll())
                .thenReturn(Fixtures.list(fixture, "cursos", CursoDto.class));
        when(cargoTipoClient.findAll())
                .thenReturn(Fixtures.list(fixture, "cargoTipos", CargoTipoDto.class));
        when(personaClient.findAllLegajos(anyList()))
                .thenReturn(Fixtures.list(fixture, "personas", PersonaDto.class));

        List<CursoCargoDto> cursoCargos = Fixtures.list(fixture, "cursoCargos", CursoCargoDto.class);
        // Una sola consulta al período: ya no se piden los cursos por legajo.
        when(cursoCargoClient.findAllByPeriodo(2026, 9)).thenReturn(cursoCargos);
    }
}
