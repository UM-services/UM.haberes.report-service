package um.haberes.report.reports.excluidos.application.usecases;

import java.nio.file.Path;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import um.haberes.report.reports.excluidos.domain.model.ExcluidosContent;
import um.haberes.report.reports.excluidos.domain.ports.in.BuildExcluidosContentUseCase;
import um.haberes.report.reports.excluidos.domain.ports.in.GenerateExcluidosXlsxUseCase;
import um.haberes.report.reports.excluidos.domain.ports.out.ExcluidosXlsxRenderer;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

@Component
@RequiredArgsConstructor
public class GenerateExcluidosXlsxUseCaseImpl implements GenerateExcluidosXlsxUseCase {

    static final String DISPLAY_NAME = "excluidos.xlsx";

    private final BuildExcluidosContentUseCase buildContent;
    private final ExcluidosXlsxRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer anho, Integer mes) {
        ExcluidosContent content = buildContent.build(anho, mes);
        Path target = storage.resolve(fileName(anho, mes));
        renderer.render(content, target);
        return storage.load(target, DISPLAY_NAME, ReportFile.Format.XLSX);
    }

    private String fileName(Integer anho, Integer mes) {
        return "excluidos." + anho + "." + mes + ".xlsx";
    }
}
