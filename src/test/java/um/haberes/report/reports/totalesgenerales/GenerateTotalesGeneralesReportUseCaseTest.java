package um.haberes.report.reports.totalesgenerales;

import org.junit.jupiter.api.Test;
import um.haberes.report.reports.totalesgenerales.application.usecases.GenerateTotalesGeneralesReportUseCaseImpl;
import um.haberes.report.reports.totalesgenerales.domain.model.TotalesGeneralesContent;
import um.haberes.report.reports.totalesgenerales.domain.ports.in.BuildTotalesGeneralesContentUseCase;
import um.haberes.report.reports.totalesgenerales.domain.ports.out.TotalesGeneralesPdfRenderer;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** El PDF se genera a partir del contenido compartido (misma fuente que tabla y XLSX). */
class GenerateTotalesGeneralesReportUseCaseTest {

    private static final Path TARGET = Path.of("target/test-output/totales.generales..2026.9.pdf");

    private final BuildTotalesGeneralesContentUseCase buildContent =
            mock(BuildTotalesGeneralesContentUseCase.class);
    private final TotalesGeneralesPdfRenderer renderer = mock(TotalesGeneralesPdfRenderer.class);
    private final ReportStorage storage = mock(ReportStorage.class);
    private final GenerateTotalesGeneralesReportUseCaseImpl useCase =
            new GenerateTotalesGeneralesReportUseCaseImpl(buildContent, renderer, storage);

    @Test
    void renderizaElContenidoDelBuilderYDevuelveElPdf() {
        TotalesGeneralesContent content = new TotalesGeneralesContent(2026, 9, List.of(
                new TotalesGeneralesContent.Grupo("Total", List.of(
                        new TotalesGeneralesContent.Fila("100", "Sueldo Básico", "1234567.89")),
                        "1.234.567,89")));
        when(buildContent.build(2026, 9)).thenReturn(content);
        when(storage.resolve("totales.generales..2026.9.pdf")).thenReturn(TARGET);
        ReportFile expected = new ReportFile(TARGET, "totales_generales.pdf",
                ReportFile.Format.PDF, 100);
        when(storage.load(any(), any(), any())).thenReturn(expected);

        ReportFile file = useCase.generate(2026, 9);

        assertThat(file).isEqualTo(expected);
        verify(renderer).render(content, TARGET);
    }
}
