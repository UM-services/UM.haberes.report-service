package um.haberes.report.reports.cargosclase;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CargoClaseDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.ClaseDto;
import um.haberes.report.support.AbstractReportIntegrationTest;
import um.haberes.report.support.Fixtures;
import um.haberes.report.support.GoldenFiles;
import um.haberes.report.support.ReportHttp;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Reporte "Cargos con Clase" ({@code GET /cargos/cargos-clase}).
 *
 * <p>Contrato: PDF attachment {@code cargos-clase.pdf}. Contenido: una seccion por clase
 * (en el orden que devuelve el core) con sus cargos ordenados alfabeticamente; los cargos
 * cuya clase no existe se omiten.
 */
class CargosClaseReportTest extends AbstractReportIntegrationTest {

    private static final String URL = "/api/haberes/report/cargos/cargos-clase";

    @Test
    void generaPdfConClasesYCargosOrdenados() throws Exception {
        JsonNode fixture = Fixtures.node("cargosclase", "normal");
        when(claseClient.findAll()).thenReturn(Fixtures.list(fixture, "clases", ClaseDto.class));
        when(cargoClaseClient.findAll()).thenReturn(Fixtures.list(fixture, "cargosClase", CargoClaseDto.class));

        MvcResult result = mockMvc.perform(get(URL)).andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "cargos-clase.pdf");
        GoldenFiles.assertPdf("cargosclase", "normal", pdf);
    }

    @Test
    void generaPdfValidoSinDatos() throws Exception {
        JsonNode fixture = Fixtures.node("cargosclase", "vacio");
        when(claseClient.findAll()).thenReturn(Fixtures.list(fixture, "clases", ClaseDto.class));
        when(cargoClaseClient.findAll()).thenReturn(Fixtures.list(fixture, "cargosClase", CargoClaseDto.class));

        MvcResult result = mockMvc.perform(get(URL)).andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "cargos-clase.pdf");
        GoldenFiles.assertPdf("cargosclase", "vacio", pdf);
    }
}
