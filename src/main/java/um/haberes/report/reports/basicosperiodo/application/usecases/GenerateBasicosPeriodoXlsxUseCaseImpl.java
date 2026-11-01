package um.haberes.report.reports.basicosperiodo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.basicosperiodo.domain.model.BasicosPeriodoContent;
import um.haberes.report.reports.basicosperiodo.domain.ports.in.BuildBasicosPeriodoContentUseCase;
import um.haberes.report.reports.basicosperiodo.domain.ports.in.GenerateBasicosPeriodoXlsxUseCase;
import um.haberes.report.reports.basicosperiodo.domain.ports.out.BasicosPeriodoXlsxRenderer;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class GenerateBasicosPeriodoXlsxUseCaseImpl implements GenerateBasicosPeriodoXlsxUseCase {

    static final String DISPLAY_NAME = "basicosperiodo.xlsx";

    private final BuildBasicosPeriodoContentUseCase buildContent;
    private final BasicosPeriodoXlsxRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer anho, Integer mes) {
        BasicosPeriodoContent content = buildContent.build(anho, mes);
        Path target = storage.resolve("basicosperiodo." + anho + "." + mes + ".xlsx");
        renderer.render(content, target);
        return storage.load(target, DISPLAY_NAME, ReportFile.Format.XLSX);
    }
}
