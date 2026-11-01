package um.haberes.report.reports.liquidables.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.liquidables.domain.model.LiquidablesContent;
import um.haberes.report.reports.liquidables.domain.ports.in.BuildLiquidablesContentUseCase;
import um.haberes.report.reports.liquidables.domain.ports.in.GenerateLiquidablesXlsxUseCase;
import um.haberes.report.reports.liquidables.domain.ports.out.LiquidablesXlsxRenderer;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class GenerateLiquidablesXlsxUseCaseImpl implements GenerateLiquidablesXlsxUseCase {

    static final String DISPLAY_NAME = "liquidables.xlsx";

    private final BuildLiquidablesContentUseCase buildContent;
    private final LiquidablesXlsxRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate() {
        LiquidablesContent content = buildContent.build();
        Path target = storage.resolve("liquidables.xlsx");
        renderer.render(content, target);
        return storage.load(target, DISPLAY_NAME, ReportFile.Format.XLSX);
    }
}
