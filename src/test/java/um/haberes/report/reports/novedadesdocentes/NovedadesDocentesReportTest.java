package um.haberes.report.reports.novedadesdocentes;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CursoCargoNovedadDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.FacultadDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.GeograficaDto;
import um.haberes.report.support.AbstractReportIntegrationTest;
import um.haberes.report.support.Fixtures;
import um.haberes.report.support.GoldenFiles;
import um.haberes.report.support.ReportHttp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Reporte "Novedades Docentes"
 * ({@code GET /novedades/novedadesDocentes/{facultadId}/{anho}/{mes}}).
 *
 * <p>Contrato: PDF attachment {@code novedadesDocentes.pdf}. Contenido: una seccion por sede
 * con novedades, y dentro de cada una los bloques ALTA / CAMBIO / BAJA en ese orden; un bloque
 * solo aparece si tiene filas.
 */
class NovedadesDocentesReportTest extends AbstractReportIntegrationTest {

    private static final String URL = "/api/haberes/report/novedades/novedadesDocentes/2/2026/9";

    @Test
    void generaPdfConNovedadesPorSede() throws Exception {
        JsonNode fixture = Fixtures.node("novedadesdocentes", "normal");
        stub(fixture);

        MvcResult result = mockMvc.perform(get(URL)).andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "novedadesDocentes.pdf");
        GoldenFiles.assertPdf("novedadesdocentes", "normal", pdf);
    }

    /** Sin novedades el service igual cierra el documento: PDF valido con solo el encabezado. */
    @Test
    void generaPdfValidoSinNovedades() throws Exception {
        JsonNode fixture = Fixtures.node("novedadesdocentes", "vacio");
        stub(fixture);

        MvcResult result = mockMvc.perform(get(URL)).andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "novedadesDocentes.pdf");
        GoldenFiles.assertPdf("novedadesdocentes", "vacio", pdf);
    }

    private void stub(JsonNode fixture) {
        when(facultadClient.findByFacultadId(2))
                .thenReturn(Fixtures.one(fixture, "facultad", FacultadDto.class));
        when(cursoCargoNovedadClient.findAllByFacultad(2, 2026, 9))
                .thenReturn(Fixtures.list(fixture, "novedades", CursoCargoNovedadDto.class));
        when(geograficaClient.findAllByGeograficaIdIn(anyList()))
                .thenReturn(Fixtures.list(fixture, "geograficas", GeograficaDto.class));

        for (GeograficaDto sede : Fixtures.list(fixture, "geograficas", GeograficaDto.class)) {
            Integer geograficaId = sede.getGeograficaId();
            when(cursoCargoNovedadClient.findAllByFacultadAndGeograficaAndAlta(2, geograficaId, 2026, 9))
                    .thenReturn(Fixtures.list(fixture, "altas" + geograficaId, CursoCargoNovedadDto.class));
            when(cursoCargoNovedadClient.findAllByFacultadAndGeograficaAndCambio(2, geograficaId, 2026, 9))
                    .thenReturn(Fixtures.list(fixture, "cambios" + geograficaId, CursoCargoNovedadDto.class));
            when(cursoCargoNovedadClient.findAllByFacultadAndGeograficaAndBaja(2, geograficaId, 2026, 9))
                    .thenReturn(Fixtures.list(fixture, "bajas" + geograficaId, CursoCargoNovedadDto.class));
        }
    }
}
