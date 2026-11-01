package um.haberes.report.reports.cursodocente.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.cursodocente.domain.model.CursoDocenteContent;
import um.haberes.report.reports.cursodocente.domain.ports.in.BuildCursoDocenteContentUseCase;
import um.haberes.report.reports.cursodocente.domain.ports.in.GenerateCursoDocenteReportUseCase;
import um.haberes.report.reports.cursodocente.domain.ports.out.CursoDocentePdfRenderer;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class GenerateCursoDocenteReportUseCaseImpl implements GenerateCursoDocenteReportUseCase {

    static final String DISPLAY_NAME = "curso-docente.pdf";

    private final BuildCursoDocenteContentUseCase buildContent;
    private final CursoDocentePdfRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer anho, Integer mes) {
        CursoDocenteContent content = buildContent.build(anho, mes);
        Path target = storage.resolve(fileName(anho, mes));
        renderer.render(content, target);
        return storage.load(target, DISPLAY_NAME, ReportFile.Format.PDF);
    }

    private String fileName(Integer anho, Integer mes) {
        return "curso-docente.." + anho + "." + mes + ".pdf";
    }
}
