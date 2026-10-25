package um.haberes.report.reports.librosueldos.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.librosueldos.domain.model.LibroSueldosContent;
import um.haberes.report.reports.librosueldos.domain.ports.in.BuildLibroSueldosContentUseCase;
import um.haberes.report.reports.librosueldos.domain.ports.in.GenerateLibroSueldosReportUseCase;
import um.haberes.report.reports.librosueldos.domain.ports.out.LibroSueldosPdfRenderer;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class GenerateLibroSueldosReportUseCaseImpl implements GenerateLibroSueldosReportUseCase {

    static final String DISPLAY_NAME = "librosueldos.pdf";

    private final BuildLibroSueldosContentUseCase buildContent;
    private final LibroSueldosPdfRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer anho, Integer mes) {
        LibroSueldosContent content = buildContent.build(anho, mes);
        Path target = storage.resolve("librosueldos." + anho + "." + mes + ".pdf");
        renderer.render(content, target);
        return storage.load(target, DISPLAY_NAME, ReportFile.Format.PDF);
    }
}
