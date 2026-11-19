package um.haberes.report.reports.excluidos.application.usecases;

import java.nio.file.Path;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import um.haberes.report.reports.excluidos.domain.model.ExcluidosContent;
import um.haberes.report.reports.excluidos.domain.ports.in.BuildExcluidosContentUseCase;
import um.haberes.report.reports.excluidos.domain.ports.in.GenerateExcluidosPdfUseCase;
import um.haberes.report.reports.excluidos.domain.ports.out.ExcluidosPdfRenderer;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

@Component
@RequiredArgsConstructor
public class GenerateExcluidosPdfUseCaseImpl implements GenerateExcluidosPdfUseCase {

    static final String DISPLAY_NAME = "excluidos.pdf";

    private final BuildExcluidosContentUseCase buildContent;
    private final ExcluidosPdfRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer anho, Integer mes) {
        ExcluidosContent content = buildContent.build(anho, mes);
        Path target = storage.resolve(fileName(anho, mes));
        renderer.render(content, target);
        return storage.load(target, DISPLAY_NAME, ReportFile.Format.PDF);
    }

    private String fileName(Integer anho, Integer mes) {
        return "excluidos." + anho + "." + mes + ".pdf";
    }
}
