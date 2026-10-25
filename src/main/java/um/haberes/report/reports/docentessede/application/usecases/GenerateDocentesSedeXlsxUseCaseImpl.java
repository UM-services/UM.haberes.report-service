package um.haberes.report.reports.docentessede.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.docentessede.domain.model.DocentesSedeContent;
import um.haberes.report.reports.docentessede.domain.ports.in.BuildDocentesSedeContentUseCase;
import um.haberes.report.reports.docentessede.domain.ports.in.GenerateDocentesSedeXlsxUseCase;
import um.haberes.report.reports.docentessede.domain.ports.out.DocentesSedeXlsxRenderer;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class GenerateDocentesSedeXlsxUseCaseImpl implements GenerateDocentesSedeXlsxUseCase {

    static final String DISPLAY_NAME = "docentesSede.xlsx";

    private final BuildDocentesSedeContentUseCase buildContent;
    private final DocentesSedeXlsxRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer facultadId, Integer geograficaId, Integer anho, Integer mes) {
        DocentesSedeContent content = buildContent.build(facultadId, geograficaId, anho, mes);
        Path target = storage.resolve(
                "docentes." + facultadId + "." + geograficaId + "." + anho + "." + mes + ".xlsx");
        renderer.render(content, target);
        return storage.load(target, DISPLAY_NAME, ReportFile.Format.XLSX);
    }
}
