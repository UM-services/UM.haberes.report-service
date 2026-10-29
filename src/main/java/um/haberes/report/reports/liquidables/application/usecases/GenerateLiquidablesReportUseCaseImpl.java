package um.haberes.report.reports.liquidables.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.liquidables.domain.model.LiquidablesContent;
import um.haberes.report.reports.liquidables.domain.ports.in.BuildLiquidablesContentUseCase;
import um.haberes.report.reports.liquidables.domain.ports.in.GenerateLiquidablesReportUseCase;
import um.haberes.report.reports.liquidables.domain.ports.out.LiquidablesPdfRenderer;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class GenerateLiquidablesReportUseCaseImpl implements GenerateLiquidablesReportUseCase {

    static final String DISPLAY_NAME = "liquidables.pdf";

    private final BuildLiquidablesContentUseCase buildContent;
    private final LiquidablesPdfRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate() {
        LiquidablesContent content = buildContent.build();
        Path target = storage.resolve("liquidables.pdf");
        renderer.render(content, target);
        return storage.load(target, DISPLAY_NAME, ReportFile.Format.PDF);
    }
}
