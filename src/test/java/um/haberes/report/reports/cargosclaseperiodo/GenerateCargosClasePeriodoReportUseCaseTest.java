package um.haberes.report.reports.cargosclaseperiodo;

import org.junit.jupiter.api.Test;
import um.haberes.report.reports.cargosclaseperiodo.application.usecases.GenerateCargosClasePeriodoReportUseCaseImpl;
import um.haberes.report.reports.cargosclaseperiodo.domain.model.CargosClasePeriodoContent;
import um.haberes.report.reports.cargosclaseperiodo.domain.ports.in.BuildCargosClasePeriodoContentUseCase;
import um.haberes.report.reports.cargosclaseperiodo.domain.ports.out.CargosClasePeriodoPdfRenderer;
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
class GenerateCargosClasePeriodoReportUseCaseTest {

    private static final Path TARGET = Path.of("target/test-output/cargos-clase-periodo..2.20269..pdf");

    private final BuildCargosClasePeriodoContentUseCase buildContent =
            mock(BuildCargosClasePeriodoContentUseCase.class);
    private final CargosClasePeriodoPdfRenderer renderer =
            mock(CargosClasePeriodoPdfRenderer.class);
    private final ReportStorage storage = mock(ReportStorage.class);
    private final GenerateCargosClasePeriodoReportUseCaseImpl useCase =
            new GenerateCargosClasePeriodoReportUseCaseImpl(buildContent, renderer, storage);

    @Test
    void renderizaElContenidoDelBuilderYDevuelveElPdf() {
        CargosClasePeriodoContent content =
                new CargosClasePeriodoContent(2026, 9, "Facultad de Ingeniería", List.of());
        when(buildContent.build(2, 2026, 9)).thenReturn(content);
        when(storage.resolve("cargos-clase-periodo..2.20269..pdf")).thenReturn(TARGET);
        ReportFile expected = new ReportFile(TARGET, "cargos-clase-periodo.pdf",
                ReportFile.Format.PDF, 100);
        when(storage.load(any(), any(), any())).thenReturn(expected);

        ReportFile file = useCase.generate(2, 2026, 9);

        assertThat(file).isEqualTo(expected);
        verify(renderer).render(content, TARGET);
    }
}
