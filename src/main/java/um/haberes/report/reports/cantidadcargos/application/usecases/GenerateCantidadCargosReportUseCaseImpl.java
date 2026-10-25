package um.haberes.report.reports.cantidadcargos.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.cantidadcargos.domain.model.CantidadCargosContent;
import um.haberes.report.reports.cantidadcargos.domain.ports.in.BuildCantidadCargosContentUseCase;
import um.haberes.report.reports.cantidadcargos.domain.ports.in.GenerateCantidadCargosReportUseCase;
import um.haberes.report.reports.cantidadcargos.domain.ports.out.CantidadCargosPdfRenderer;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class GenerateCantidadCargosReportUseCaseImpl implements GenerateCantidadCargosReportUseCase {

    static final String DISPLAY_NAME = "cantidadcargos.pdf";

    private final BuildCantidadCargosContentUseCase buildContent;
    private final CantidadCargosPdfRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer anho, Integer mes) {
        CantidadCargosContent content = buildContent.build(anho, mes);
        Path target = storage.resolve(fileName(anho, mes));
        renderer.render(content, target);
        return storage.load(target, DISPLAY_NAME, ReportFile.Format.PDF);
    }

    private String fileName(Integer anho, Integer mes) {
        return "cantidadcargos." + anho + "." + mes + ".pdf";
    }
}
