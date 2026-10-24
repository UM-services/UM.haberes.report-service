package um.haberes.report.reports.totalescodigo;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CodigoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.TotalItemDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.TotalNovedadDto;
import um.haberes.report.support.AbstractReportIntegrationTest;
import um.haberes.report.support.Fixtures;
import um.haberes.report.support.GoldenFiles;
import um.haberes.report.support.ReportHttp;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

class TotalesCodigoReportTest extends AbstractReportIntegrationTest {

    private static final String URL = "/api/haberes/report/totalesCodigo/totalesCodigo/2026/9";

    @Test
    void generaPdfSoloConCodigosConNovedad() throws Exception {
        stub(Fixtures.node("totalescodigo", "normal"));
        MvcResult result = mockMvc.perform(get(URL)).andReturn();
        byte[] pdf = ReportHttp.assertAttachment(result, "totales_codigo.pdf");
        GoldenFiles.assertPdf("totalescodigo", "normal", pdf);
    }

    @Test
    void generaPdfValidoSinDatos() throws Exception {
        stub(Fixtures.node("totalescodigo", "vacio"));
        MvcResult result = mockMvc.perform(get(URL)).andReturn();
        byte[] pdf = ReportHttp.assertAttachment(result, "totales_codigo.pdf");
        GoldenFiles.assertPdf("totalescodigo", "vacio", pdf);
    }

    private void stub(JsonNode fixture) {
        when(totalNovedadClient.findAllByPeriodo(2026, 9))
                .thenReturn(Fixtures.list(fixture, "totalNovedades", TotalNovedadDto.class));
        when(totalItemClient.findAllByPeriodo(2026, 9))
                .thenReturn(Fixtures.list(fixture, "totalItems", TotalItemDto.class));
        when(codigoClient.findAllByPeriodo(2026, 9))
                .thenReturn(Fixtures.list(fixture, "codigos", CodigoDto.class));
    }
}
