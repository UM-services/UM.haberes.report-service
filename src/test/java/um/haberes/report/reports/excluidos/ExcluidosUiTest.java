package um.haberes.report.reports.excluidos;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.ExcluidoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.ItemDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.PersonaDto;
import um.haberes.report.support.AbstractReportIntegrationTest;
import um.haberes.report.support.GoldenFiles;
import um.haberes.report.support.ReportHttp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/** Namespace del frontend para el listado de excluidos de la acreditación. */
class ExcluidosUiTest extends AbstractReportIntegrationTest {

    private static final String BASE = "/api/haberes/report/ui/excluidos";
    private static final String API_KEY = "test-api-key";
    private static final String HEADER = "X-API-Key";

    @Test
    void devuelveLosExcluidosDelPeriodo() throws Exception {
        stubNormal();

        MvcResult result = mockMvc.perform(get(BASE + "/data/2026/9").header(HEADER, API_KEY))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        var json = new ObjectMapper().readTree(result.getResponse().getContentAsString());
        assertThat(json.get("anho").asInt()).isEqualTo(2026);
        assertThat(json.get("mes").asInt()).isEqualTo(9);
        assertThat(json.get("filas")).hasSize(2);

        var gomez = json.get("filas").get(0);
        assertThat(gomez.get("legajoId").asText()).isEqualTo("1001");
        assertThat(gomez.get("apellidoNombre").asText()).isEqualTo("Gómez, Ana");
        assertThat(gomez.get("importe").asText()).isNotEmpty();
        assertThat(gomez.get("observaciones").asText()).isEqualTo("No percibe por banco");
        assertThat(gomez.get("fecha").asText()).isEqualTo("20/09/2026");

        assertThat(json.get("filas").get(1).get("legajoId").asText()).isEqualTo("1002");
    }

    @Test
    void devuelveElPeriodoSinExcluidos() throws Exception {
        when(excluidoClient.findAllByPeriodo(2026, 10)).thenReturn(List.of());

        MvcResult result = mockMvc.perform(get(BASE + "/data/2026/10").header(HEADER, API_KEY))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        var json = new ObjectMapper().readTree(result.getResponse().getContentAsString());
        assertThat(json.get("filas")).isEmpty();
    }

    @Test
    void descargaElPdfDeExcluidos() throws Exception {
        stubNormal();

        MvcResult result = mockMvc.perform(get(BASE + "/pdf/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "excluidos.pdf");
        GoldenFiles.assertPdf("excluidos", "normal", pdf);
    }

    @Test
    void descargaElXlsxDeExcluidos() throws Exception {
        stubNormal();

        MvcResult result = mockMvc.perform(get(BASE + "/xlsx/2026/9").header(HEADER, API_KEY))
                .andReturn();

        byte[] xlsx = ReportHttp.assertAttachment(result, "excluidos.xlsx");
        GoldenFiles.assertWorkbook("excluidos", "normal-xlsx", xlsx);
    }

    private void stubNormal() {
        ExcluidoDto primero = new ExcluidoDto(1L, 1001L, 2026, 9,
                OffsetDateTime.parse("2026-09-20T00:00:00Z"), "No percibe por banco");
        ExcluidoDto segundo = new ExcluidoDto(2L, 1002L, 2026, 9,
                OffsetDateTime.parse("2026-09-21T00:00:00Z"), "");
        when(excluidoClient.findAllByPeriodo(2026, 9)).thenReturn(List.of(primero, segundo));

        when(personaClient.findAllLegajos(anyList())).thenReturn(List.of(
                persona(1001L, "Gómez", "Ana"),
                persona(1002L, "Pérez", "Juan")));

        when(itemClient.findAllByCodigoId(99, 2026, 9)).thenReturn(List.of(
                item(1001L, new BigDecimal("1234567.89")),
                item(1002L, new BigDecimal("50000.5"))));
    }

    private PersonaDto persona(Long legajoId, String apellido, String nombre) {
        PersonaDto persona = new PersonaDto();
        persona.setLegajoId(legajoId);
        persona.setApellido(apellido);
        persona.setNombre(nombre);
        return persona;
    }

    private ItemDto item(Long legajoId, BigDecimal importe) {
        ItemDto item = new ItemDto();
        item.setLegajoId(legajoId);
        item.setAnho(2026);
        item.setMes(9);
        item.setCodigoId(99);
        item.setImporte(importe);
        return item;
    }
}
