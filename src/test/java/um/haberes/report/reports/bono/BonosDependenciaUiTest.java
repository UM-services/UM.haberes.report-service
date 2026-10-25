package um.haberes.report.reports.bono;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.ContactoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.FormularioImprimirDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.LegajoControlDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.LiquidacionDto;
import um.haberes.report.support.AbstractReportIntegrationTest;
import um.haberes.report.support.BonoStubs;
import um.haberes.report.support.Fixtures;
import um.haberes.report.support.GoldenFiles;
import um.haberes.report.support.ReportHttp;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Namespace del frontend para el bono por dependencia: grilla, PDF consolidado y envío.
 *
 * <p>La auditoría de la impresión vive en el core ({@code print-prepare}); estas rutas no deben
 * registrar {@code bono_impresion} por sí mismas.
 */
class BonosDependenciaUiTest extends AbstractReportIntegrationTest {

    private static final String BASE = "/api/haberes/report/ui/bonoDependencia";
    private static final String API_KEY = "test-api-key";
    private static final String HEADER = "X-API-Key";
    private static final int ANHO = 2026;
    private static final int MES = 9;
    private static final int DEPENDENCIA = 4;
    private static final String SALIDA = "A";

    private final BonoStubs stubs = new BonoStubs(this);

    @Test
    void devuelveLaGrillaDeLaDependencia() throws Exception {
        when(formularioImprimirClient.findData(ANHO, MES, DEPENDENCIA, SALIDA))
                .thenReturn(formulario());

        MvcResult result = mockMvc.perform(
                get(BASE + "/data/2026/9/4/A").header(HEADER, API_KEY)).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        var json = new ObjectMapper().readTree(result.getResponse().getContentAsString());
        assertThat(json.get("dependenciaId").asText()).isEqualTo("4");
        assertThat(json.get("salida").asText()).isEqualTo("A");
        assertThat(json.get("filas")).hasSize(2);
        var primera = json.get("filas").get(0);
        assertThat(primera.get("legajoId").asText()).isEqualTo("120");
        assertThat(primera.get("apellidoNombre").asText()).isEqualTo("Gómez, Ana");
        assertThat(primera.get("mailInstitucional").asText()).isEqualTo("ana.gomez@um.edu.ar");
        assertThat(primera.get("bonoEnviado").asBoolean()).isFalse();
        assertThat(json.get("filas").get(1).get("bonoEnviado").asBoolean()).isTrue();
    }

    @Test
    void descargaElPdfConsolidadoSinAuditar() throws Exception {
        JsonNode fixture = Fixtures.node("bono", "normal");
        stubs.stubComun(fixture);
        stubs.stubLegajo(fixture, 120L);
        stubs.stubLegajo(fixture, 121L);
        when(liquidacionClient.findAllByDependencia(DEPENDENCIA, ANHO, MES, SALIDA))
                .thenReturn(Fixtures.list(fixture, "liquidaciones", LiquidacionDto.class));

        MvcResult result = mockMvc.perform(
                get(BASE + "/pdf/2026/9/4/A").header(HEADER, API_KEY)).andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "bonos.pdf");
        GoldenFiles.assertPdf("bono", "dependencia", pdf);

        verify(bonoImpresionClient, never()).add(any());
    }

    @Test
    void enviaUnBonoSinAuditar() throws Exception {
        JsonNode fixture = Fixtures.node("bono", "normal");
        stubs.stubComun(fixture);
        stubs.stubLegajo(fixture, 120L);
        stubs.stubMail();

        MvcResult result = mockMvc.perform(
                get(BASE + "/sendBono/120/2026/9").header(HEADER, API_KEY)).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getResponse().getContentAsString()).isEqualTo("Envío de Correo Ok!!");
        verify(bonoImpresionClient, never()).add(any());
        verify(javaMailSender).send(any(jakarta.mail.internet.MimeMessage.class));
    }

    @Test
    void devuelve400ConElMensajeDeNegocioSiNoHayLiquidaciones() throws Exception {
        when(liquidacionClient.findAllByDependencia(99999, ANHO, MES, SALIDA))
                .thenReturn(List.of());

        MvcResult result = mockMvc.perform(
                get(BASE + "/pdf/2026/9/99999/A").header(HEADER, API_KEY)).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(400);
        var json = new ObjectMapper().readTree(result.getResponse().getContentAsString());
        assertThat(json.get("detail").asText()).contains("No hay liquidaciones para la dependencia");
    }

    private FormularioImprimirDto formulario() {
        LegajoControlDto enviado = new LegajoControlDto(2L, 121L, ANHO, MES, (byte) 1, (byte) 0,
                (byte) 1, null);
        LegajoControlDto pendiente = new LegajoControlDto(1L, 120L, ANHO, MES, (byte) 1, (byte) 0,
                (byte) 0, persona(120L, "Gómez", "Ana"));
        ContactoDto contacto = new ContactoDto(120L, "", "", "", "ana.gomez@um.edu.ar");
        return new FormularioImprimirDto(List.of(pendiente, enviado), List.of(contacto));
    }

    private um.haberes.report.shared.infrastructure.client.haberes.core.dto.PersonaDto persona(
            Long legajoId, String apellido, String nombre) {
        var persona = new um.haberes.report.shared.infrastructure.client.haberes.core.dto.PersonaDto();
        persona.setLegajoId(legajoId);
        persona.setApellido(apellido);
        persona.setNombre(nombre);
        return persona;
    }
}
