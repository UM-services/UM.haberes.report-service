package um.haberes.report.reports.librosueldos.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.librosueldos.domain.model.LibroSueldosContent;
import um.haberes.report.reports.librosueldos.domain.ports.in.BuildLibroSueldosContentUseCase;
import um.haberes.report.reports.librosueldos.domain.ports.in.GenerateLibroSueldosXlsxUseCase;
import um.haberes.report.reports.librosueldos.domain.ports.out.LibroSueldosXlsxRenderer;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class GenerateLibroSueldosXlsxUseCaseImpl implements GenerateLibroSueldosXlsxUseCase {

    static final String DISPLAY_NAME = "librosueldos.xlsx";

    private final BuildLibroSueldosContentUseCase buildContent;
    private final LibroSueldosXlsxRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer anho, Integer mes) {
        LibroSueldosContent content = buildContent.build(anho, mes);
        Path target = storage.resolve("librosueldos." + anho + "." + mes + ".xlsx");
        renderer.render(content, target);
        return storage.load(target, DISPLAY_NAME, ReportFile.Format.XLSX);
    }
}
