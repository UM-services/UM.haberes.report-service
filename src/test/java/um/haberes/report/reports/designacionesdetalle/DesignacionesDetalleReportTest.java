package um.haberes.report.reports.designacionesdetalle;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CargoLiquidacionDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CargoTipoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CategoriaDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CursoCargoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CursoFusionDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.DependenciaDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.DesignacionTipoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.FacultadDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.GeograficaDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.NovedadDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.PersonaDto;
import um.haberes.report.support.AbstractReportIntegrationTest;
import um.haberes.report.support.Fixtures;
import um.haberes.report.support.GoldenFiles;
import um.haberes.report.support.ReportHttp;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Detalle de designaciones de un legajo
 * ({@code GET /api/haberes/report/designacionesDetalle/{legajoId}/{anho}/{mes}}).
 *
 * <p>Contrato: PDF <b>inline</b> {@code designaciones-120-9-2026.pdf} (el orden del nombre es
 * legajo-mes-anho, distinto del orden de la ruta).
 *
 * <p>Contenido: encabezado con el legajo y el periodo, y cuatro bloques: designaciones
 * liquidadas del <b>mes anterior</b>, cursos asignados del periodo, designaciones fusionadas y
 * resumen del control con la diferencia (novedad de codigo 27). Cada bloque lista una fila por
 * registro y un total, o la leyenda "Sin registros para el periodo." si la lista viene vacia.
 *
 * <p>Importes: la categoria embebida en el cargo/cursos manda sobre el catalogo; si tampoco hay
 * basico embebido se usa {@code categoriaBasico} y finalmente cero.
 */
class DesignacionesDetalleReportTest extends AbstractReportIntegrationTest {

    private static final String URL = "/api/haberes/report/designacionesDetalle/120/2026/9";

    @Test
    void generaPdfInlineConLiquidadasCursosFusionesYDiferencia() throws Exception {
        JsonNode fixture = Fixtures.node("designacionesdetalle", "normal");
        stub(fixture);

        MvcResult result = mockMvc.perform(get(URL)).andReturn();

        byte[] pdf = ReportHttp.assertInlinePdf(result, "designaciones-120-9-2026.pdf");
        GoldenFiles.assertPdf("designacionesdetalle", "normal", pdf);
    }

    @Test
    void generaPdfInlineSinDesignacionesNiCatalogos() throws Exception {
        JsonNode fixture = Fixtures.node("designacionesdetalle", "vacio");
        stub(fixture);

        MvcResult result = mockMvc.perform(get(URL)).andReturn();

        byte[] pdf = ReportHttp.assertInlinePdf(result, "designaciones-120-9-2026.pdf");
        GoldenFiles.assertPdf("designacionesdetalle", "vacio", pdf);
    }

    private void stub(JsonNode fixture) {
        when(personaClient.findByLegajoId(120L))
                .thenReturn(Fixtures.one(fixture, "persona", PersonaDto.class));
        // El periodo pedido es 9/2026: las liquidadas se consultan del mes anterior, 8/2026.
        when(cargoLiquidacionClient.findAllDocenteByLegajo(120L, 2026, 8))
                .thenReturn(Fixtures.list(fixture, "liquidadas", CargoLiquidacionDto.class));
        when(cursoCargoClient.findAllByLegajo(120L, 2026, 9))
                .thenReturn(Fixtures.list(fixture, "cursos", CursoCargoDto.class));
        when(cursoFusionClient.findAllByLegajoId(120L, 2026, 9))
                .thenReturn(Fixtures.list(fixture, "fusiones", CursoFusionDto.class));
        when(novedadClient.findByUnique(120L, 2026, 9, 27, "null"))
                .thenReturn(Fixtures.one(fixture, "novedad", NovedadDto.class));

        when(facultadClient.findAll())
                .thenReturn(Fixtures.list(fixture, "facultades", FacultadDto.class));
        when(geograficaClient.findAll())
                .thenReturn(Fixtures.list(fixture, "geograficas", GeograficaDto.class));
        when(dependenciaClient.findAll())
                .thenReturn(Fixtures.list(fixture, "dependencias", DependenciaDto.class));
        when(categoriaClient.findAll())
                .thenReturn(Fixtures.list(fixture, "categorias", CategoriaDto.class));
        when(cargoTipoClient.findAll())
                .thenReturn(Fixtures.list(fixture, "cargoTipos", CargoTipoDto.class));
        when(designacionTipoClient.findAll())
                .thenReturn(Fixtures.list(fixture, "designacionTipos", DesignacionTipoDto.class));
    }
}
