package um.haberes.report.reports.totalesgenerales;

import org.junit.jupiter.api.Test;
import um.haberes.report.reports.totalesgenerales.application.usecases.BuildTotalesGeneralesContentUseCaseImpl;
import um.haberes.report.reports.totalesgenerales.domain.model.CodigoGrupo;
import um.haberes.report.reports.totalesgenerales.domain.model.TotalMensualCodigo;
import um.haberes.report.reports.totalesgenerales.domain.model.TotalesGeneralesContent;
import um.haberes.report.reports.totalesgenerales.domain.ports.out.CodigoGrupoCatalogPort;
import um.haberes.report.reports.totalesgenerales.domain.ports.out.TotalMensualCatalogPort;
import um.haberes.report.shared.domain.model.CodigoResumen;
import um.haberes.report.shared.domain.ports.out.CodigoCatalogPort;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** Contenido compartido de "Totales Generales": agrupacion, orden fijo y subtotales. */
class BuildTotalesGeneralesContentUseCaseTest {

    private final CodigoCatalogPort codigoCatalogPort = mock(CodigoCatalogPort.class);
    private final TotalMensualCatalogPort totalMensualCatalogPort = mock(TotalMensualCatalogPort.class);
    private final CodigoGrupoCatalogPort codigoGrupoCatalogPort = mock(CodigoGrupoCatalogPort.class);
    private final BuildTotalesGeneralesContentUseCaseImpl useCase =
            new BuildTotalesGeneralesContentUseCaseImpl(codigoCatalogPort, totalMensualCatalogPort,
                    codigoGrupoCatalogPort);

    @Test
    void agrupaEnOrdenFijoSumaSubtotalesYDejaVacioElTotalAusente() {
        when(codigoCatalogPort.findAllByPeriodo(2026, 9)).thenReturn(List.of(
                codigo(100, "Sueldo Básico"), codigo(200, "Presentismo"),
                codigo(300, "Aporte Jubilatorio"), codigo(400, "Asignación Familiar")));
        when(totalMensualCatalogPort.findAllByPeriodo(2026, 9)).thenReturn(List.of(
                new TotalMensualCodigo(100, new BigDecimal("1234567.89")),
                new TotalMensualCodigo(200, new BigDecimal("98765.43")),
                new TotalMensualCodigo(300, new BigDecimal("-45678.90"))));
        when(codigoGrupoCatalogPort.findAll()).thenReturn(List.of(
                new CodigoGrupo(100, true, false, false, true),
                new CodigoGrupo(200, true, false, false, false),
                new CodigoGrupo(300, false, false, true, false),
                new CodigoGrupo(400, false, true, false, false)));

        TotalesGeneralesContent content = useCase.build(2026, 9);

        assertThat(content.anho()).isEqualTo(2026);
        assertThat(content.mes()).isEqualTo(9);
        assertThat(content.grupos()).extracting(TotalesGeneralesContent.Grupo::nombre)
                .containsExactly("Remunerativo", "No Remunerativo", "Deducción", "Total");
        assertThat(content.grupos().get(0).filas()).extracting(TotalesGeneralesContent.Fila::codigoId)
                .containsExactly("100", "200");
        assertThat(content.grupos().get(0).subtotal()).isEqualTo("1.333.333,32");
        assertThat(content.grupos().get(1).filas()).singleElement()
                .satisfies(fila -> assertThat(fila.total()).isEmpty());
        assertThat(content.grupos().get(1).subtotal()).isEqualTo("0");
        assertThat(content.grupos().get(2).subtotal()).isEqualTo("-45.678,9");
        assertThat(content.grupos().get(3).filas()).singleElement()
                .satisfies(fila -> assertThat(fila.total()).isEqualTo("1234567.89"));
    }

    @Test
    void sinGruposDevuelveContenidoVacio() {
        when(codigoCatalogPort.findAllByPeriodo(2026, 9)).thenReturn(List.of());
        when(totalMensualCatalogPort.findAllByPeriodo(2026, 9)).thenReturn(List.of());
        when(codigoGrupoCatalogPort.findAll()).thenReturn(List.of());

        TotalesGeneralesContent content = useCase.build(2026, 9);

        assertThat(content.grupos()).isEmpty();
    }

    private static CodigoResumen codigo(Integer id, String nombre) {
        return CodigoResumen.builder().codigoId(id).nombre(nombre).build();
    }
}
