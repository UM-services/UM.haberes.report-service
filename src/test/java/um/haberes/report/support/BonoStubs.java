package um.haberes.report.support;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.AntiguedadDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CargoClaseDetalleDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CargoLiquidacionDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CodigoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CodigoGrupoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.ContactoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.ControlDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CursoCargoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.DependenciaDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.ItemDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.LegajoBancoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.LegajoControlDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.LetraDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.LiquidacionAdicionalDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.PersonaDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * Stubs del fixture {@code bono} compartidos por las suites del módulo: todos los endpoints
 * piden los mismos agregados por legajo, así que conviene mantenerlos en un solo lugar.
 */
public final class BonoStubs {

    private static final int ANHO = 2026;
    private static final int MES = 9;

    private final AbstractReportIntegrationTest base;

    public BonoStubs(AbstractReportIntegrationTest base) {
        this.base = base;
    }

    /** Stubs compartidos por todos los endpoints: control, tablas maestras y grupos. */
    public void stubComun(JsonNode fixture) {
        when(base.controlClient.findByPeriodo(ANHO, MES))
                .thenReturn(Fixtures.one(fixture, "control", ControlDto.class));
        when(base.dependenciaClient.findAll())
                .thenReturn(Fixtures.list(fixture, "dependencias", DependenciaDto.class));
        when(base.dependenciaClient.findByDependenciaId(anyInt()))
                .thenAnswer(invocation -> dependencia(fixture, invocation.getArgument(0)));
        when(base.codigoClient.findByCodigoId(981))
                .thenReturn(Fixtures.one(fixture.get("codigos"), "981", CodigoDto.class));
        when(base.codigoGrupoClient.findAllByRemunerativo((byte) 1))
                .thenReturn(Fixtures.list(fixture, "codigoGruposRemunerativo", CodigoGrupoDto.class));
        when(base.codigoGrupoClient.findAllByNoRemunerativo((byte) 1))
                .thenReturn(Fixtures.list(fixture, "codigoGruposNoRemunerativo", CodigoGrupoDto.class));
        when(base.codigoGrupoClient.findAllByDeduccion((byte) 1))
                .thenReturn(Fixtures.list(fixture, "codigoGruposDeduccion", CodigoGrupoDto.class));
        // Solo se usa en la rama ETEC (codigo 29), que este fixture no ejercita.
        when(base.liquidacionEtecClient.calcularPorcentajeAntiguedad(anyLong(), anyInt(), anyInt(),
                anyInt(), anyInt())).thenReturn(new BigDecimal("0.1000"));
    }

    /** Stubs de los agregados que el recibo pide por legajo. */
    public void stubLegajo(JsonNode fixture, long legajoId) {
        String key = Long.toString(legajoId);
        when(base.personaClient.findByLegajoId(legajoId))
                .thenReturn(Fixtures.one(fixture.get("personas"), key, PersonaDto.class));
        when(base.antiguedadClient.findByUnique(legajoId, ANHO, MES))
                .thenReturn(Fixtures.one(fixture.get("antiguedades"), key, AntiguedadDto.class));
        when(base.designacionToolClient.indiceAntiguedad(legajoId, ANHO, MES))
                .thenReturn(Fixtures.list(fixture.get("indices"), key, BigDecimal.class));
        when(base.legajoBancoClient.findLegajoCbuPrincipal(legajoId, ANHO, MES))
                .thenReturn(Fixtures.one(fixture.get("legajosBanco"), key, LegajoBancoDto.class));
        when(base.itemClient.findAllByLegajo(legajoId, ANHO, MES))
                .thenReturn(Fixtures.list(fixture.get("items"), key, ItemDto.class));
        when(base.cursoCargoClient.findAllByLegajo(legajoId, ANHO, MES))
                .thenReturn(Fixtures.list(fixture.get("cursosCargo"), key, CursoCargoDto.class));
        when(base.cursoCargoClient.findAllByLegajoAndNivel(legajoId, ANHO, MES, 2))
                .thenReturn(List.of());
        when(base.cargoLiquidacionClient.findAllDocenteByLegajo(legajoId, ANHO, MES))
                .thenReturn(Fixtures.list(fixture.get("cargosDocente"), key, CargoLiquidacionDto.class));
        when(base.cargoLiquidacionClient.findAllNoDocenteByLegajo(legajoId, ANHO, MES))
                .thenReturn(Fixtures.list(fixture.get("cargosNoDocente"), key, CargoLiquidacionDto.class));
        when(base.cargoClaseDetalleClient.findAllByLegajo(legajoId, ANHO, MES))
                .thenReturn(Fixtures.list(fixture.get("cargosClase"), key, CargoClaseDetalleDto.class));
        when(base.liquidacionAdicionalClient.findAllByLegajo(legajoId, ANHO, MES))
                .thenReturn(Fixtures.list(fixture.get("adicionales"), key, LiquidacionAdicionalDto.class));
        when(base.letraClient.findByUnique(legajoId, ANHO, MES))
                .thenReturn(Fixtures.one(fixture.get("letras"), key, LetraDto.class));
        when(base.letraClient.update(any(), anyLong()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(base.contactoClient.findByLegajoId(legajoId))
                .thenReturn(Fixtures.one(fixture.get("contactos"), key, ContactoDto.class));
        when(base.legajoControlClient.findByUnique(legajoId, ANHO, MES))
                .thenReturn(Fixtures.one(fixture.get("legajosControl"), key, LegajoControlDto.class));
        when(base.legajoControlClient.update(any(), anyLong()))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    public void stubMail() {
        MimeMessage message = new MimeMessage(Session.getInstance(new Properties()));
        when(base.javaMailSender.createMimeMessage()).thenReturn(message);
    }

    private static DependenciaDto dependencia(JsonNode fixture, int dependenciaId) {
        return Fixtures.list(fixture, "dependencias", DependenciaDto.class).stream()
                .filter(dependencia -> dependencia.getDependenciaId() == dependenciaId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("dependencia " + dependenciaId));
    }
}
