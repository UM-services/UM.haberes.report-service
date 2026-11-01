package um.haberes.report.reports.basicosperiodo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CargoClaseDetalleDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CargoLiquidacionDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CodigoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.LiquidacionAdicionalDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.LiquidacionDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.PersonaDto;
import um.haberes.report.support.AbstractReportIntegrationTest;
import um.haberes.report.support.Fixtures;
import um.haberes.report.support.GoldenFiles;
import um.haberes.report.support.ReportHttp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/** Namespace del frontend para la "Planilla de Básicos por Período". */
class BasicosPeriodoUiTest extends AbstractReportIntegrationTest {

    private static final String BASE = "/api/haberes/report/ui/basicosPeriodo";
    private static final String API_KEY = "test-api-key";
    private static final String HEADER = "X-API-Key";

    @Test
    void devuelveLosBasicosDelPeriodo() throws Exception {
        stubNormal();

        MvcResult result = mockMvc.perform(get(BASE + "/data/2026/9").header(HEADER, API_KEY))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        var json = new ObjectMapper().readTree(result.getResponse().getContentAsString());
        assertThat(json.get("filas")).hasSize(4);

        var cargo = json.get("filas").get(0);
        assertThat(cargo.get("legajoId").asText()).isEqualTo("1001");
        assertThat(cargo.get("dependencia").asText()).isEqualTo("Facultad de Ingeniería");
        assertThat(cargo.get("claseCodigo").asText()).isEqualTo("1");
        assertThat(cargo.get("basico").asText()).isEqualTo("1.234.567,89");
        assertThat(cargo.get("jornada").asText()).isEqualTo("1");
        assertThat(cargo.get("horasJornada").asText()).isEmpty();

        var clase = json.get("filas").get(1);
        assertThat(clase.get("claseCodigo").asText()).isEqualTo("Clase A");
        assertThat(clase.get("categoriaCargo").asText()).isEqualTo("Ayudante / Turno mañana");
        assertThat(clase.get("basico").asText()).isEqualTo("5.000,00");
        assertThat(clase.get("jornada").asText()).isEqualTo("10");

        var porHora = json.get("filas").get(2);
        assertThat(porHora.get("legajoId").asText()).isEqualTo("1002");
        assertThat(porHora.get("horasJornada").asText()).isEqualTo("20");
        assertThat(porHora.get("jornada").asText()).isEmpty();

        var adicional = json.get("filas").get(3);
        assertThat(adicional.get("claseCodigo").asText()).isEqualTo("981");
        assertThat(adicional.get("categoriaCargo").asText()).isEqualTo("Adicional");
        assertThat(adicional.get("basico").asText()).isEqualTo("2.500,00");
        assertThat(adicional.get("jornada").asText()).isEqualTo("1");
    }

    @Test
    void descargaElPdfDelPeriodo() throws Exception {
        stubNormal();

        MvcResult result = mockMvc.perform(get(BASE + "/pdf/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "basicosperiodo.pdf");
        GoldenFiles.assertPdf("basicosperiodo", "normal", pdf);
    }

    @Test
    void descargaElXlsxDelPeriodo() throws Exception {
        stubNormal();

        MvcResult result = mockMvc.perform(get(BASE + "/xlsx/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] xlsx = ReportHttp.assertAttachment(result, "basicosperiodo.xlsx");
        GoldenFiles.assertWorkbook("basicosperiodo", "normal-xlsx", xlsx);
    }

    @Test
    void descargaElPdfSinDatos() throws Exception {
        stubVacio();

        MvcResult result = mockMvc.perform(get(BASE + "/pdf/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "basicosperiodo.pdf");
        GoldenFiles.assertPdf("basicosperiodo", "vacio", pdf);
    }

    private void stubNormal() {
        JsonNode fixture = Fixtures.node("basicosperiodo", "normal");
        when(liquidacionClient.findAllByPeriodo(2026, 9, 0))
                .thenReturn(Fixtures.list(fixture, "liquidaciones", LiquidacionDto.class));
        when(cargoLiquidacionClient.findAllByPeriodo(2026, 9))
                .thenReturn(Fixtures.list(fixture, "cargos", CargoLiquidacionDto.class));
        when(cargoClaseDetalleClient.findAllByPeriodo(2026, 9))
                .thenReturn(Fixtures.list(fixture, "cargosClase", CargoClaseDetalleDto.class));
        when(liquidacionAdicionalClient.findAllByPeriodo(2026, 9))
                .thenReturn(Fixtures.list(fixture, "adicionales", LiquidacionAdicionalDto.class));
        when(personaClient.findAllLegajos(anyList()))
                .thenReturn(Fixtures.list(fixture, "personas", PersonaDto.class));
        when(codigoClient.findByCodigoId(981))
                .thenReturn(Fixtures.one(fixture, "codigo", CodigoDto.class));
    }

    private void stubVacio() {
        JsonNode fixture = Fixtures.node("basicosperiodo", "vacio");
        when(liquidacionClient.findAllByPeriodo(2026, 9, 0))
                .thenReturn(Fixtures.list(fixture, "liquidaciones", LiquidacionDto.class));
    }
}
