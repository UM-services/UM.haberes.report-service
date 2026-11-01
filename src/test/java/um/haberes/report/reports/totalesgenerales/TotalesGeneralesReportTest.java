package um.haberes.report.reports.totalesgenerales;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CodigoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CodigoGrupoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.TotalMensualDto;
import um.haberes.report.support.AbstractReportIntegrationTest;
import um.haberes.report.support.Fixtures;
import um.haberes.report.support.GoldenFiles;
import um.haberes.report.support.ReportHttp;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Reporte "Totales Generales" ({@code GET /totalesGenerales/totalesGenerales/{anho}/{mes}}).
 *
 * <p>Contrato: PDF attachment {@code totales_generales.pdf}. Contenido: una tabla por grupo
 * (Remunerativo, No Remunerativo, Deducción, Total) en ese orden fijo, con subtotal por grupo;
 * los códigos que no pertenecen a ningún grupo no se imprimen.
 */
class TotalesGeneralesReportTest extends AbstractReportIntegrationTest {

    private static final String URL = "/api/haberes/report/totalesGenerales/totalesGenerales/2026/9";

    @Test
    void generaPdfConGruposYSubtotales() throws Exception {
        JsonNode fixture = Fixtures.node("totalesgenerales", "normal");
        stub(fixture);

        MvcResult result = mockMvc.perform(get(URL)).andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "totales_generales.pdf");
        GoldenFiles.assertPdf("totalesgenerales", "normal", pdf);
    }

    @Test
    void generaPdfValidoSinDatos() throws Exception {
        JsonNode fixture = Fixtures.node("totalesgenerales", "vacio");
        stub(fixture);

        MvcResult result = mockMvc.perform(get(URL)).andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "totales_generales.pdf");
        GoldenFiles.assertPdf("totalesgenerales", "vacio", pdf);
    }

    private void stub(JsonNode fixture) {
        when(totalMensualClient.findAllByPeriodo(2026, 9))
                .thenReturn(Fixtures.list(fixture, "totalMensuales", TotalMensualDto.class));
        when(codigoClient.findAllByPeriodo(2026, 9))
                .thenReturn(Fixtures.list(fixture, "codigos", CodigoDto.class));
        when(codigoGrupoClient.findAll())
                .thenReturn(Fixtures.list(fixture, "codigoGrupos", CodigoGrupoDto.class));
    }
}
