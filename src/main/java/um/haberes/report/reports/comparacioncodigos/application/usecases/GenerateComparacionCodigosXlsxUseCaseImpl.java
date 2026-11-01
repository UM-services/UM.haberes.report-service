package um.haberes.report.reports.comparacioncodigos.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.comparacioncodigos.domain.model.ComparacionCodigosContent;
import um.haberes.report.reports.comparacioncodigos.domain.ports.in.BuildComparacionCodigosContentUseCase;
import um.haberes.report.reports.comparacioncodigos.domain.ports.in.GenerateComparacionCodigosXlsxUseCase;
import um.haberes.report.reports.comparacioncodigos.domain.ports.out.ComparacionCodigosXlsxRenderer;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class GenerateComparacionCodigosXlsxUseCaseImpl implements GenerateComparacionCodigosXlsxUseCase {

    static final String FILE_NAME = "comparacion_codigos.xlsx";

    private final BuildComparacionCodigosContentUseCase buildContent;
    private final ComparacionCodigosXlsxRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer anho, Integer mes) {
        ComparacionCodigosContent content = buildContent.build(anho, mes);
        Path target = storage.resolve("comparacion.codigos." + anho + "." + mes + ".xlsx");
        renderer.render(anho, mes, content, target);
        return storage.load(target, FILE_NAME, ReportFile.Format.XLSX);
    }
}
