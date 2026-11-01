package um.haberes.report.reports.comparacionremuneraciones;

import org.junit.jupiter.api.Test;
import um.haberes.report.reports.comparacionremuneraciones.application.usecases.GenerateComparacionRemuneracionesReportUseCaseImpl;
import um.haberes.report.reports.comparacionremuneraciones.domain.model.ComparacionRemuneracionesContent;
import um.haberes.report.reports.comparacionremuneraciones.domain.ports.in.BuildComparacionRemuneracionesContentUseCase;
import um.haberes.report.reports.comparacionremuneraciones.domain.ports.out.ComparacionRemuneracionesPdfRenderer;
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
class GenerateComparacionRemuneracionesReportUseCaseTest {

    private static final Path TARGET =
            Path.of("target/test-output/comparacion-remuneraciones.2026.9.2026.8.pdf");

    private final BuildComparacionRemuneracionesContentUseCase buildContent =
            mock(BuildComparacionRemuneracionesContentUseCase.class);
    private final ComparacionRemuneracionesPdfRenderer renderer =
            mock(ComparacionRemuneracionesPdfRenderer.class);
    private final ReportStorage storage = mock(ReportStorage.class);
    private final GenerateComparacionRemuneracionesReportUseCaseImpl useCase =
            new GenerateComparacionRemuneracionesReportUseCaseImpl(buildContent, renderer, storage);

    @Test
    void renderizaElContenidoDelBuilderYDevuelveElPdf() {
        ComparacionRemuneracionesContent content =
                new ComparacionRemuneracionesContent(2026, 9, 2026, 8, List.of());
        when(buildContent.build(2026, 9, 2026, 8)).thenReturn(content);
        when(storage.resolve("comparacion-remuneraciones.2026.9.2026.8.pdf")).thenReturn(TARGET);
        ReportFile expected = new ReportFile(TARGET, "comparacion-remuneraciones.pdf",
                ReportFile.Format.PDF, 100);
        when(storage.load(any(), any(), any())).thenReturn(expected);

        ReportFile file = useCase.generate(2026, 9, 2026, 8);

        assertThat(file).isEqualTo(expected);
        verify(renderer).render(content, TARGET);
    }
}
