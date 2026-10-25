package um.haberes.report.reports.cargosclaseperiodo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CargoClaseDetalleDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CargoClaseDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CargoClasePeriodoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.ClaseDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.FacultadDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.GeograficaDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.PersonaDto;
import um.haberes.report.support.AbstractReportIntegrationTest;
import um.haberes.report.support.Fixtures;
import um.haberes.report.support.GoldenFiles;
import um.haberes.report.support.ReportHttp;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/** Namespace del frontend para "Cargos con Clase por Facultad". */
class CargosClasePeriodoUiTest extends AbstractReportIntegrationTest {

    private static final String BASE = "/api/haberes/report/ui/cargosClasePeriodo";
    private static final String API_KEY = "test-api-key";
    private static final String HEADER = "X-API-Key";

    @Test
    void devuelveLosCargosAgrupadosPorSede() throws Exception {
        stub(Fixtures.node("cargosclaseperiodo", "normal"));

        MvcResult result = mockMvc
                .perform(get(BASE + "/data/2/2026/9").header(HEADER, API_KEY))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        var json = new ObjectMapper().readTree(result.getResponse().getContentAsString());
        assertThat(json.get("facultad").asText()).isEqualTo("Facultad de Ingeniería");
        assertThat(json.get("sedes")).hasSize(2);
        assertThat(json.get("sedes").get(0).get("nombre").asText())
                .isEqualTo("Campus Universitario");
        var fila = json.get("sedes").get(0).get("filas").get(0);
        assertThat(fila.get("legajoId").asLong()).isEqualTo(1001);
        assertThat(fila.get("cargo").asText()).isEqualTo("Profesor Titular");
        assertThat(fila.get("periodoDesde").asLong()).isEqualTo(202601L);
        assertThat(fila.get("clase").asText()).isEqualTo("Primera");
    }

    @Test
    void descargaElPdfDelPeriodo() throws Exception {
        stub(Fixtures.node("cargosclaseperiodo", "normal"));

        MvcResult result = mockMvc
                .perform(get(BASE + "/pdf/2/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "cargos-clase-periodo.pdf");
        GoldenFiles.assertPdf("cargosclaseperiodo", "normal", pdf);
    }

    @Test
    void descargaElXlsxDelPeriodo() throws Exception {
        stub(Fixtures.node("cargosclaseperiodo", "normal"));

        MvcResult result = mockMvc
                .perform(get(BASE + "/xlsx/2/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] xlsx = ReportHttp.assertAttachment(result, "cargos-clase-periodo.xlsx");
        GoldenFiles.assertWorkbook("cargosclaseperiodo", "normal-xlsx", xlsx);
    }

    private void stub(JsonNode fixture) {
        when(facultadClient.findByFacultadId(2))
                .thenReturn(Fixtures.one(fixture, "facultad", FacultadDto.class));
        when(personaClient.findAllByFacultad(2))
                .thenReturn(Fixtures.list(fixture, "personas", PersonaDto.class));
        when(geograficaClient.findAll())
                .thenReturn(Fixtures.list(fixture, "geograficas", GeograficaDto.class));
        when(claseClient.findAll())
                .thenReturn(Fixtures.list(fixture, "clases", ClaseDto.class));

        List<CargoClaseDetalleDto> detalles = new ArrayList<>();
        List<CargoClasePeriodoDto> periodos = new ArrayList<>();
        for (PersonaDto persona : Fixtures.list(fixture, "personas", PersonaDto.class)) {
            Long legajoId = persona.getLegajoId();
            detalles.addAll(Fixtures.list(fixture, "detalles" + legajoId, CargoClaseDetalleDto.class));
            periodos.addAll(Fixtures.list(fixture, "periodos" + legajoId, CargoClasePeriodoDto.class));
        }
        when(cargoClaseDetalleClient.findAllByFacultad(2, 2026, 9)).thenReturn(detalles);
        when(cargoClasePeriodoClient.findAllByFacultad(2)).thenReturn(periodos);

        for (CargoClaseDto cargoClase : Fixtures.list(fixture, "cargoClases", CargoClaseDto.class)) {
            when(cargoClaseClient.findByCargoClaseId(cargoClase.getCargoClaseId())).thenReturn(cargoClase);
        }
    }
}
