package um.haberes.report.reports.fusiondocente.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.fusiondocente.domain.model.FusionDocenteContent;
import um.haberes.report.reports.fusiondocente.domain.ports.in.BuildFusionDocenteContentUseCase;
import um.haberes.report.reports.fusiondocente.domain.ports.in.GenerateFusionDocenteReportUseCase;
import um.haberes.report.reports.fusiondocente.domain.ports.out.FusionDocentePdfRenderer;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class GenerateFusionDocenteReportUseCaseImpl implements GenerateFusionDocenteReportUseCase {

    static final String DISPLAY_NAME = "fusion.docente.pdf";

    private final BuildFusionDocenteContentUseCase buildContent;
    private final FusionDocentePdfRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer anho, Integer mes) {
        FusionDocenteContent content = buildContent.build(anho, mes);
        Path target = storage.resolve(fileName(anho, mes));
        renderer.render(content, target);
        return storage.load(target, DISPLAY_NAME, ReportFile.Format.PDF);
    }

    private String fileName(Integer anho, Integer mes) {
        return "fusion.docente.." + anho + "." + mes + ".pdf";
    }
}
