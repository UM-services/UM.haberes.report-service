package um.haberes.report.reports.cursoscontratado.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.cursoscontratado.domain.model.CursosContratadoContent;
import um.haberes.report.reports.cursoscontratado.domain.ports.in.BuildCursosContratadoContentUseCase;
import um.haberes.report.reports.cursoscontratado.domain.ports.in.GenerateCursosContratadoReportUseCase;
import um.haberes.report.reports.cursoscontratado.domain.ports.out.CursosContratadoPdfRenderer;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class GenerateCursosContratadoReportUseCaseImpl implements GenerateCursosContratadoReportUseCase {

    static final String DISPLAY_NAME = "cursoscontratado.pdf";

    private final BuildCursosContratadoContentUseCase buildContent;
    private final CursosContratadoPdfRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer anho, Integer mes) {
        CursosContratadoContent content = buildContent.build(anho, mes);
        Path target = storage.resolve("cursoscontratado." + anho + "." + mes + ".pdf");
        renderer.render(content, target);
        return storage.load(target, DISPLAY_NAME, ReportFile.Format.PDF);
    }
}
