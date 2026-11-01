package um.haberes.report.reports.cantidadcargos.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.cantidadcargos.domain.model.CantidadCargosContent;
import um.haberes.report.reports.cantidadcargos.domain.ports.in.BuildCantidadCargosContentUseCase;
import um.haberes.report.reports.cantidadcargos.domain.ports.in.GenerateCantidadCargosXlsxUseCase;
import um.haberes.report.reports.cantidadcargos.domain.ports.out.CantidadCargosXlsxRenderer;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class GenerateCantidadCargosXlsxUseCaseImpl implements GenerateCantidadCargosXlsxUseCase {

    static final String DISPLAY_NAME = "cantidadcargos.xlsx";

    private final BuildCantidadCargosContentUseCase buildContent;
    private final CantidadCargosXlsxRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer anho, Integer mes) {
        CantidadCargosContent content = buildContent.build(anho, mes);
        Path target = storage.resolve(fileName(anho, mes));
        renderer.render(content, target);
        return storage.load(target, DISPLAY_NAME, ReportFile.Format.XLSX);
    }

    private String fileName(Integer anho, Integer mes) {
        return "cantidadcargos." + anho + "." + mes + ".xlsx";
    }
}
