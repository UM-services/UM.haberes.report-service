package um.haberes.report.reports.anotador;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.AnotadorDto;
import um.haberes.report.support.AbstractReportIntegrationTest;
import um.haberes.report.support.Fixtures;
import um.haberes.report.support.GoldenFiles;
import um.haberes.report.support.ReportHttp;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Modulo "Anotador" - cuatro endpoints con dos ejes: alcance (una facultad vs. todas) y
 * estado (pendientes vs. autorizados/rechazados).
 *
 * <p>Contrato: PDF attachment cuyo nombre es siempre {@code anotadorPendientes.pdf} o
 * {@code anotadorAutorizadosRechazados.pdf}, sin importar el alcance.
 *
 * <p>Contenido: encabezado institucional (imagen + periodo) y, por cada anotador, legajo,
 * documento, apellido y nombre, facultad, fecha de creacion, usuario, estado derivado de los
 * flags {@code autorizado}/{@code rechazado}, anotacion y respuesta.
 *
 * <p>Comportamientos legacy que se congelan:
 * <ul>
 *   <li>Una respuesta nula se imprime literalmente como {@code null}.</li>
 *   <li>La variante de autorizados/rechazados imprime siempre el titulo "Rechazados", aunque
 *       la lista venga vacia.</li>
 *   <li>Sin registros igual se emite un PDF valido: el service no retorna antes de cerrar el
 *       documento.</li>
 * </ul>
 */
class AnotadorReportTest extends AbstractReportIntegrationTest {

    private static final String BASE = "/api/haberes/report/anotador";

    @Test
    void generaPdfDePendientesDeUnaFacultad() throws Exception {
        JsonNode fixture = Fixtures.node("anotador", "normal");
        when(anotadorClient.findPendientesByFacultad(2, 2026, 9))
                .thenReturn(Fixtures.list(fixture, "pendientes", AnotadorDto.class));

        MvcResult result = mockMvc.perform(get(BASE + "/anotadorPendientes/2/2026/9")).andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "anotadorPendientes.pdf");
        GoldenFiles.assertPdf("anotador", "pendientes-facultad-normal", pdf);
    }

    @Test
    void generaPdfDePendientesDelPeriodo() throws Exception {
        JsonNode fixture = Fixtures.node("anotador", "normal");
        when(anotadorClient.findPendientes(2026, 9))
                .thenReturn(Fixtures.list(fixture, "pendientes", AnotadorDto.class));

        MvcResult result = mockMvc.perform(get(BASE + "/anotadorPendientes/periodo/2026/9")).andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "anotadorPendientes.pdf");
        GoldenFiles.assertPdf("anotador", "pendientes-periodo-normal", pdf);
    }

    @Test
    void generaPdfValidoSinPendientes() throws Exception {
        JsonNode fixture = Fixtures.node("anotador", "vacio");
        when(anotadorClient.findPendientesByFacultad(2, 2026, 9))
                .thenReturn(Fixtures.list(fixture, "pendientes", AnotadorDto.class));

        MvcResult result = mockMvc.perform(get(BASE + "/anotadorPendientes/2/2026/9")).andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "anotadorPendientes.pdf");
        GoldenFiles.assertPdf("anotador", "pendientes-facultad-vacio", pdf);
    }

    @Test
    void generaPdfValidoSinPendientesEnElPeriodo() throws Exception {
        JsonNode fixture = Fixtures.node("anotador", "vacio");
        when(anotadorClient.findPendientes(2026, 9))
                .thenReturn(Fixtures.list(fixture, "pendientes", AnotadorDto.class));

        MvcResult result = mockMvc.perform(get(BASE + "/anotadorPendientes/periodo/2026/9")).andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "anotadorPendientes.pdf");
        GoldenFiles.assertPdf("anotador", "pendientes-periodo-vacio", pdf);
    }

    @Test
    void generaPdfDeAutorizadosYRechazadosDeUnaFacultad() throws Exception {
        JsonNode fixture = Fixtures.node("anotador", "normal");
        when(anotadorClient.findAutorizadosByFacultad(2, 2026, 9))
                .thenReturn(Fixtures.list(fixture, "autorizados", AnotadorDto.class));
        when(anotadorClient.findRechazadosByFacultad(2, 2026, 9))
                .thenReturn(Fixtures.list(fixture, "rechazados", AnotadorDto.class));

        MvcResult result = mockMvc.perform(get(BASE + "/anotadorAutorizadosRechazados/2/2026/9")).andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "anotadorAutorizadosRechazados.pdf");
        GoldenFiles.assertPdf("anotador", "autorizados-rechazados-facultad-normal", pdf);
    }

    @Test
    void generaPdfDeAutorizadosYRechazadosDelPeriodo() throws Exception {
        JsonNode fixture = Fixtures.node("anotador", "normal");
        when(anotadorClient.findRevisados(2026, 9))
                .thenReturn(Fixtures.list(fixture, "revisados", AnotadorDto.class));

        MvcResult result = mockMvc.perform(get(BASE + "/anotadorAutorizadosRechazados/periodo/2026/9")).andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "anotadorAutorizadosRechazados.pdf");
        GoldenFiles.assertPdf("anotador", "autorizados-rechazados-periodo-normal", pdf);
    }

    @Test
    void generaPdfValidoSinAutorizadosNiRechazados() throws Exception {
        JsonNode fixture = Fixtures.node("anotador", "vacio");
        when(anotadorClient.findAutorizadosByFacultad(2, 2026, 9))
                .thenReturn(Fixtures.list(fixture, "autorizados", AnotadorDto.class));
        when(anotadorClient.findRechazadosByFacultad(2, 2026, 9))
                .thenReturn(Fixtures.list(fixture, "rechazados", AnotadorDto.class));

        MvcResult result = mockMvc.perform(get(BASE + "/anotadorAutorizadosRechazados/2/2026/9")).andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "anotadorAutorizadosRechazados.pdf");
        GoldenFiles.assertPdf("anotador", "autorizados-rechazados-facultad-vacio", pdf);
    }

    @Test
    void generaPdfValidoSinRevisadosEnElPeriodo() throws Exception {
        JsonNode fixture = Fixtures.node("anotador", "vacio");
        when(anotadorClient.findRevisados(2026, 9))
                .thenReturn(Fixtures.list(fixture, "revisados", AnotadorDto.class));

        MvcResult result = mockMvc.perform(get(BASE + "/anotadorAutorizadosRechazados/periodo/2026/9")).andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "anotadorAutorizadosRechazados.pdf");
        GoldenFiles.assertPdf("anotador", "autorizados-rechazados-periodo-vacio", pdf);
    }
}
