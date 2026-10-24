package um.haberes.report.reports.bono;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.web.servlet.MvcResult;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.AntiguedadDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.BonoImpresionDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CargoClaseDetalleDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CargoLiquidacionDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CodigoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CodigoGrupoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.ContactoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.ControlDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CursoCargoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.DependenciaDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.FacultadDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.ItemDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.LegajoBancoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.LegajoControlDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.LetraDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.LiquidacionAdicionalDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.LiquidacionDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.PersonaDto;
import um.haberes.report.support.AbstractReportIntegrationTest;
import um.haberes.report.support.Fixtures;
import um.haberes.report.support.GoldenFiles;
import um.haberes.report.support.ReportHttp;

import java.math.BigDecimal;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Modulo "Bono": recibo de haberes individual, detalle de cargos, lote por dependencia y
 * envio por correo.
 *
 * <p>Contratos congelados:
 * <ul>
 *   <li>{@code /ui/generatePdf} devuelve el PDF <b>inline</b> con nombre
 *       {@code apellido.nombre.legajo.anho.mes.pdf} y <b>no</b> registra auditoria.</li>
 *   <li>{@code /generatePdf}, {@code /detalleCargos} y {@code /generatePdfDependencia}
 *       devuelven un attachment ({@code bono.pdf} / {@code bono.pdf} / {@code bonos.pdf}).</li>
 *   <li>{@code /generatePdf} y {@code /sendBono} auditan via {@code bonoImpresionClient.add};
 *       las variantes {@code /ui/*} no.</li>
 *   <li>Ambos {@code generatePdf} borran los items en cero ({@code deleteAllByZero}) y
 *       congelan la letra del neto ({@code letraClient.update}); si la letra no existe caen a
 *       {@code letraClient.add}.</li>
 *   <li>Ambos {@code sendBono} devuelven literalmente {@code "Envío de Correo Ok!!"} y marcan
 *       {@code LegajoControl.bonoEnviado = 1}.</li>
 * </ul>
 */
class BonoReportTest extends AbstractReportIntegrationTest {

    private static final String BASE = "/api/haberes/report/bono";
    private static final int ANHO = 2026;
    private static final int MES = 9;
    private static final long LEGAJO = 120L;
    private static final long LEGAJO_DEPENDENCIA = 121L;
    private static final long LEGAJO_SOLICITUD = 555L;
    private static final String IP = "10.0.0.7";
    private static final int DEPENDENCIA = 4;
    private static final int FACULTAD = 2;

    // ---------------------------------------------------------------- endpoints

    @Test
    void generaPdfInlineParaFrontendSinAuditar() throws Exception {
        JsonNode fixture = Fixtures.node("bono", "normal");
        stubComun(fixture);
        stubLegajo(fixture, LEGAJO);

        MvcResult result = mockMvc.perform(get(BASE + "/ui/generatePdf/120/2026/9")).andReturn();

        byte[] pdf = ReportHttp.assertInlinePdf(result, "Gómez.Ana.120.2026.9.pdf");
        GoldenFiles.assertPdf("bono", "ui-generatePdf", pdf);

        verify(bonoImpresionClient, never()).add(any());
        verify(itemClient).deleteAllByZero(LEGAJO, ANHO, MES);
        verify(letraClient).update(any(), anyLong());
    }

    @Test
    void generaPdfConAuditoria() throws Exception {
        JsonNode fixture = Fixtures.node("bono", "normal");
        stubComun(fixture);
        stubLegajo(fixture, LEGAJO);

        MvcResult result = mockMvc.perform(
                get(BASE + "/generatePdf/120/2026/9/555/10.0.0.7")).andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "bono.pdf");
        GoldenFiles.assertPdf("bono", "generatePdf", pdf);

        verify(bonoImpresionClient).add(any());
        verify(itemClient).deleteAllByZero(LEGAJO, ANHO, MES);

        ArgumentCaptor<BonoImpresionDto> auditoria = ArgumentCaptor.forClass(BonoImpresionDto.class);
        verify(bonoImpresionClient).add(auditoria.capture());
        assertThat(auditoria.getValue().getLegajoId()).isEqualTo(LEGAJO);
        assertThat(auditoria.getValue().getAnho()).isEqualTo(ANHO);
        assertThat(auditoria.getValue().getMes()).isEqualTo(MES);
        assertThat(auditoria.getValue().getLegajoIdSolicitud()).isEqualTo(LEGAJO_SOLICITUD);
        assertThat(auditoria.getValue().getIpAddress()).isEqualTo(IP);

        ArgumentCaptor<LetraDto> letra = ArgumentCaptor.forClass(LetraDto.class);
        verify(letraClient).update(letra.capture(), eq(7L));
        assertThat(letra.getValue().getNeto()).isEqualByComparingTo(new BigDecimal("1650000.00"));
        assertThat(letra.getValue().getCadena())
                .isEqualTo("un millon seiscientos cincuenta mil con 00/100");
    }

    @Test
    void generaDetalleDeCargos() throws Exception {
        JsonNode fixture = Fixtures.node("bono", "normal");
        stubComun(fixture);
        stubLegajo(fixture, LEGAJO);
        when(facultadClient.findByFacultadId(FACULTAD))
                .thenReturn(Fixtures.one(fixture.get("facultades"), "2", FacultadDto.class));
        when(cursoCargoClient.findAllByFacultad(LEGAJO, ANHO, MES, FACULTAD))
                .thenReturn(list(fixture, "cursosCargo", "120", CursoCargoDto.class));
        when(cargoLiquidacionClient.findAllDocenteByLegajoAndFacultad(LEGAJO, ANHO, MES, FACULTAD))
                .thenReturn(list(fixture, "cargosDocente", "120", CargoLiquidacionDto.class));
        when(cargoLiquidacionClient.findAllNoDocenteByLegajoAndFacultad(LEGAJO, ANHO, MES, FACULTAD))
                .thenReturn(list(fixture, "cargosNoDocente", "120", CargoLiquidacionDto.class));
        when(cargoClaseDetalleClient.findAllByLegajoAndFacultad(LEGAJO, ANHO, MES, FACULTAD))
                .thenReturn(list(fixture, "cargosClase", "120", CargoClaseDetalleDto.class));

        MvcResult result = mockMvc.perform(
                get(BASE + "/detalleCargos/120/2026/9/2")).andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "bono.pdf");
        GoldenFiles.assertPdf("bono", "detalleCargos", pdf);

        verify(bonoImpresionClient, never()).add(any());
    }

    @Test
    void generaPdfDeTodaUnaDependencia() throws Exception {
        JsonNode fixture = Fixtures.node("bono", "normal");
        stubComun(fixture);
        stubLegajo(fixture, LEGAJO);
        stubLegajo(fixture, LEGAJO_DEPENDENCIA);
        when(liquidacionClient.findAllByDependencia(DEPENDENCIA, ANHO, MES, "docente"))
                .thenReturn(Fixtures.list(fixture, "liquidaciones", LiquidacionDto.class));

        MvcResult result = mockMvc.perform(
                get(BASE + "/generatePdfDependencia/2026/9/4/docente/555/10.0.0.7")).andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "bonos.pdf");
        GoldenFiles.assertPdf("bono", "dependencia", pdf);

        verify(bonoImpresionClient, times(2)).add(any());
        verify(itemClient).deleteAllByZero(LEGAJO, ANHO, MES);
        verify(itemClient).deleteAllByZero(LEGAJO_DEPENDENCIA, ANHO, MES);
    }

    @Test
    void enviaBonoYAudita() throws Exception {
        JsonNode fixture = Fixtures.node("bono", "normal");
        stubComun(fixture);
        stubLegajo(fixture, LEGAJO);
        stubMail();

        MvcResult result = mockMvc.perform(
                get(BASE + "/sendBono/120/2026/9/555/10.0.0.7")).andReturn();

        String body = result.getResponse().getContentAsString();
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(body).isEqualTo("Envío de Correo Ok!!");
        GoldenFiles.assertText("bono", "sendBono-ok", body);

        verify(bonoImpresionClient).add(any());
        verify(letraClient).update(any(), anyLong());
        verify(javaMailSender).send(any(MimeMessage.class));
        ArgumentCaptor<LegajoControlDto> control = ArgumentCaptor.forClass(LegajoControlDto.class);
        verify(legajoControlClient).update(control.capture(), eq(301L));
        assertThat(control.getValue().getBonoEnviado()).isEqualTo((byte) 1);
    }

    @Test
    void enviaBonoDesdeFrontendSinAuditar() throws Exception {
        JsonNode fixture = Fixtures.node("bono", "normal");
        stubComun(fixture);
        stubLegajo(fixture, LEGAJO);
        stubMail();

        MvcResult result = mockMvc.perform(get(BASE + "/ui/sendBono/120/2026/9")).andReturn();

        String body = result.getResponse().getContentAsString();
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(body).isEqualTo("Envío de Correo Ok!!");
        GoldenFiles.assertText("bono", "ui-sendBono-ok", body);

        verify(bonoImpresionClient, never()).add(any());
        verify(javaMailSender).send(any(MimeMessage.class));
        verify(legajoControlClient).update(any(), eq(301L));
    }

    /** Legacy: si la letra del periodo no existe, el recibo se genera igual y se registra. */
    @Test
    void generaPdfCuandoLaLetraNoExisteYRegistraUnaNueva() throws Exception {
        JsonNode fixture = Fixtures.node("bono", "normal");
        stubComun(fixture);
        stubLegajo(fixture, LEGAJO);
        when(letraClient.findByUnique(LEGAJO, ANHO, MES))
                .thenThrow(new RuntimeException("letra inexistente"));
        when(letraClient.add(any())).thenAnswer(invocation -> invocation.getArgument(0));

        MvcResult result = mockMvc.perform(
                get(BASE + "/generatePdf/120/2026/9/555/10.0.0.7")).andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "bono.pdf");
        GoldenFiles.assertPdf("bono", "generatePdf-letra-nueva", pdf);

        verify(letraClient, never()).update(any(), anyLong());
        verify(letraClient).add(any());
        verify(bonoImpresionClient).add(any());
    }

    // ---------------------------------------------------------------- stubs

    /** Stubs compartidos por todos los endpoints: control, tablas maestras y grupos. */
    private void stubComun(JsonNode fixture) {
        when(controlClient.findByPeriodo(ANHO, MES))
                .thenReturn(Fixtures.one(fixture, "control", ControlDto.class));
        when(dependenciaClient.findAll())
                .thenReturn(Fixtures.list(fixture, "dependencias", DependenciaDto.class));
        when(dependenciaClient.findByDependenciaId(anyInt()))
                .thenAnswer(invocation -> dependencia(fixture, invocation.getArgument(0)));
        when(codigoClient.findByCodigoId(981))
                .thenReturn(Fixtures.one(fixture.get("codigos"), "981", CodigoDto.class));
        when(codigoGrupoClient.findAllByRemunerativo((byte) 1))
                .thenReturn(Fixtures.list(fixture, "codigoGruposRemunerativo", CodigoGrupoDto.class));
        when(codigoGrupoClient.findAllByNoRemunerativo((byte) 1))
                .thenReturn(Fixtures.list(fixture, "codigoGruposNoRemunerativo", CodigoGrupoDto.class));
        when(codigoGrupoClient.findAllByDeduccion((byte) 1))
                .thenReturn(Fixtures.list(fixture, "codigoGruposDeduccion", CodigoGrupoDto.class));
        // Solo se usa en la rama ETEC (codigo 29), que este fixture no ejercita.
        when(liquidacionEtecClient.calcularPorcentajeAntiguedad(anyLong(), anyInt(), anyInt(), anyInt(), anyInt()))
                .thenReturn(new BigDecimal("0.1000"));
    }

    /** Stubs de los agregados que el recibo pide por legajo. */
    private void stubLegajo(JsonNode fixture, long legajoId) {
        String key = Long.toString(legajoId);
        when(personaClient.findByLegajoId(legajoId))
                .thenReturn(Fixtures.one(fixture.get("personas"), key, PersonaDto.class));
        when(antiguedadClient.findByUnique(legajoId, ANHO, MES))
                .thenReturn(Fixtures.one(fixture.get("antiguedades"), key, AntiguedadDto.class));
        when(designacionToolClient.indiceAntiguedad(legajoId, ANHO, MES))
                .thenReturn(Fixtures.list(fixture.get("indices"), key, BigDecimal.class));
        when(legajoBancoClient.findLegajoCbuPrincipal(legajoId, ANHO, MES))
                .thenReturn(Fixtures.one(fixture.get("legajosBanco"), key, LegajoBancoDto.class));
        when(itemClient.findAllByLegajo(legajoId, ANHO, MES))
                .thenReturn(Fixtures.list(fixture.get("items"), key, ItemDto.class));
        when(cursoCargoClient.findAllByLegajo(legajoId, ANHO, MES))
                .thenReturn(Fixtures.list(fixture.get("cursosCargo"), key, CursoCargoDto.class));
        when(cursoCargoClient.findAllByLegajoAndNivel(legajoId, ANHO, MES, 2))
                .thenReturn(List.of());
        when(cargoLiquidacionClient.findAllDocenteByLegajo(legajoId, ANHO, MES))
                .thenReturn(Fixtures.list(fixture.get("cargosDocente"), key, CargoLiquidacionDto.class));
        when(cargoLiquidacionClient.findAllNoDocenteByLegajo(legajoId, ANHO, MES))
                .thenReturn(Fixtures.list(fixture.get("cargosNoDocente"), key, CargoLiquidacionDto.class));
        when(cargoClaseDetalleClient.findAllByLegajo(legajoId, ANHO, MES))
                .thenReturn(Fixtures.list(fixture.get("cargosClase"), key, CargoClaseDetalleDto.class));
        when(liquidacionAdicionalClient.findAllByLegajo(legajoId, ANHO, MES))
                .thenReturn(Fixtures.list(fixture.get("adicionales"), key, LiquidacionAdicionalDto.class));
        when(letraClient.findByUnique(legajoId, ANHO, MES))
                .thenReturn(Fixtures.one(fixture.get("letras"), key, LetraDto.class));
        when(letraClient.update(any(), anyLong()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(contactoClient.findByLegajoId(legajoId))
                .thenReturn(Fixtures.one(fixture.get("contactos"), key, ContactoDto.class));
        when(legajoControlClient.findByUnique(legajoId, ANHO, MES))
                .thenReturn(Fixtures.one(fixture.get("legajosControl"), key, LegajoControlDto.class));
        when(legajoControlClient.update(any(), anyLong()))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    private void stubMail() {
        MimeMessage message = new MimeMessage(Session.getInstance(new Properties()));
        when(javaMailSender.createMimeMessage()).thenReturn(message);
    }

    private static DependenciaDto dependencia(JsonNode fixture, int dependenciaId) {
        return Fixtures.list(fixture, "dependencias", DependenciaDto.class).stream()
                .filter(dependencia -> dependencia.getDependenciaId() == dependenciaId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("dependencia " + dependenciaId));
    }

    private static <T> List<T> list(JsonNode fixture, String group, String key, Class<T> type) {
        return Fixtures.list(fixture.get(group), key, type);
    }
}
