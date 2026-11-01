package um.haberes.report.reports.cursodocente.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.cursodocente.domain.model.CursoDocenteContent;
import um.haberes.report.reports.cursodocente.domain.ports.in.BuildCursoDocenteContentUseCase;
import um.haberes.report.reports.cursodocente.domain.ports.in.GenerateCursoDocenteXlsxUseCase;
import um.haberes.report.reports.cursodocente.domain.ports.out.CursoDocenteXlsxRenderer;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class GenerateCursoDocenteXlsxUseCaseImpl implements GenerateCursoDocenteXlsxUseCase {

    static final String DISPLAY_NAME = "curso-docente.xlsx";

    private final BuildCursoDocenteContentUseCase buildContent;
    private final CursoDocenteXlsxRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer anho, Integer mes) {
        CursoDocenteContent content = buildContent.build(anho, mes);
        Path target = storage.resolve("curso-docente." + anho + "." + mes + ".xlsx");
        renderer.render(content, target);
        return storage.load(target, DISPLAY_NAME, ReportFile.Format.XLSX);
    }
}
