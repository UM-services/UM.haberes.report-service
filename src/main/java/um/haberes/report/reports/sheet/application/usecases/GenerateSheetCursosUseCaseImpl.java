package um.haberes.report.reports.sheet.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.sheet.domain.model.SheetCursosContent;
import um.haberes.report.reports.sheet.domain.ports.in.BuildSheetCursosContentUseCase;
import um.haberes.report.reports.sheet.domain.ports.in.GenerateSheetCursosUseCase;
import um.haberes.report.reports.sheet.domain.ports.out.SheetCursosXlsxRenderer;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class GenerateSheetCursosUseCaseImpl implements GenerateSheetCursosUseCase {

    static final String DISPLAY_NAME = "cursos.xlsx";

    private final BuildSheetCursosContentUseCase buildContent;
    private final SheetCursosXlsxRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer anho, Integer mes) {
        SheetCursosContent content = buildContent.build(anho, mes);
        Path target = storage.resolve(fileName(anho, mes));
        renderer.render(content, target);
        return storage.load(target, DISPLAY_NAME, ReportFile.Format.XLSX);
    }

    private String fileName(Integer anho, Integer mes) {
        return "cursos" + anho + String.format("%02d", mes) + ".xlsx";
    }
}
