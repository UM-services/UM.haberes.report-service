package um.haberes.report.reports.comparacionremuneraciones.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.comparacionremuneraciones.domain.model.ComparacionRemuneracionesContent;
import um.haberes.report.reports.comparacionremuneraciones.domain.ports.in.BuildComparacionRemuneracionesContentUseCase;
import um.haberes.report.reports.comparacionremuneraciones.domain.ports.in.GenerateComparacionRemuneracionesXlsxUseCase;
import um.haberes.report.reports.comparacionremuneraciones.domain.ports.out.ComparacionRemuneracionesXlsxRenderer;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class GenerateComparacionRemuneracionesXlsxUseCaseImpl
        implements GenerateComparacionRemuneracionesXlsxUseCase {

    static final String DISPLAY_NAME = "comparacion-remuneraciones.xlsx";

    private final BuildComparacionRemuneracionesContentUseCase buildContent;
    private final ComparacionRemuneracionesXlsxRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer anho, Integer mes, Integer anhoAnterior, Integer mesAnterior) {
        ComparacionRemuneracionesContent content =
                buildContent.build(anho, mes, anhoAnterior, mesAnterior);
        Path target = storage.resolve(fileName(anho, mes, anhoAnterior, mesAnterior));
        renderer.render(content, target);
        return storage.load(target, DISPLAY_NAME, ReportFile.Format.XLSX);
    }

    private String fileName(Integer anho, Integer mes, Integer anhoAnterior, Integer mesAnterior) {
        return "comparacion-remuneraciones." + anho + "." + mes + "." + anhoAnterior + "."
                + mesAnterior + ".xlsx";
    }
}
