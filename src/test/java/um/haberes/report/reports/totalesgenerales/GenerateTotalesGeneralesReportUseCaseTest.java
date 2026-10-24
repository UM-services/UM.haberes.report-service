package um.haberes.report.reports.totalesgenerales;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import um.haberes.report.reports.totalesgenerales.application.usecases.GenerateTotalesGeneralesReportUseCaseImpl;
import um.haberes.report.reports.totalesgenerales.domain.model.CodigoGrupo;
import um.haberes.report.reports.totalesgenerales.domain.model.TotalMensualCodigo;
import um.haberes.report.reports.totalesgenerales.domain.model.TotalesGeneralesContent;
import um.haberes.report.reports.totalesgenerales.domain.ports.out.CodigoGrupoCatalogPort;
import um.haberes.report.reports.totalesgenerales.domain.ports.out.TotalMensualCatalogPort;
import um.haberes.report.reports.totalesgenerales.domain.ports.out.TotalesGeneralesPdfRenderer;
import um.haberes.report.shared.domain.model.CodigoResumen;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.CodigoCatalogPort;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Caso de uso de "Totales Generales": agrupacion, orden fijo y subtotales. */
class GenerateTotalesGeneralesReportUseCaseTest {

    private static final Path TARGET = Path.of("target/test-output/totales.generales..2026.9.pdf");

    private final CodigoCatalogPort codigoCatalogPort = mock(CodigoCatalogPort.class);
    private final TotalMensualCatalogPort totalMensualCatalogPort = mock(TotalMensualCatalogPort.class);
    private final CodigoGrupoCatalogPort codigoGrupoCatalogPort = mock(CodigoGrupoCatalogPort.class);
    private final TotalesGeneralesPdfRenderer renderer = mock(TotalesGeneralesPdfRenderer.class);
    private final ReportStorage storage = mock(ReportStorage.class);
    private final GenerateTotalesGeneralesReportUseCaseImpl useCase =
            new GenerateTotalesGeneralesReportUseCaseImpl(codigoCatalogPort, totalMensualCatalogPort,
                    codigoGrupoCatalogPort, renderer, storage);

    @Test
    void agrupaEnOrdenFijoSumaSubtotalesYDejaVacioElTotalAusente() {
        stubStorage();
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

        useCase.generate(2026, 9);

        ArgumentCaptor<TotalesGeneralesContent> captor =
                ArgumentCaptor.forClass(TotalesGeneralesContent.class);
        verify(renderer).render(captor.capture(), eq(TARGET));
        TotalesGeneralesContent content = captor.getValue();

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
    void sinGruposNoGeneraTablas() {
        stubStorage();
        when(codigoCatalogPort.findAllByPeriodo(2026, 9)).thenReturn(List.of());
        when(totalMensualCatalogPort.findAllByPeriodo(2026, 9)).thenReturn(List.of());
        when(codigoGrupoCatalogPort.findAll()).thenReturn(List.of());

        useCase.generate(2026, 9);

        ArgumentCaptor<TotalesGeneralesContent> captor =
                ArgumentCaptor.forClass(TotalesGeneralesContent.class);
        verify(renderer).render(captor.capture(), eq(TARGET));
        assertThat(captor.getValue().grupos()).isEmpty();
    }

    private void stubStorage() {
        when(storage.resolve("totales.generales..2026.9.pdf")).thenReturn(TARGET);
        when(storage.load(any(), any(), any())).thenReturn(
                new ReportFile(TARGET, "totales_generales.pdf", ReportFile.Format.PDF, 100));
    }

    private static CodigoResumen codigo(Integer id, String nombre) {
        return CodigoResumen.builder().codigoId(id).nombre(nombre).build();
    }
}
