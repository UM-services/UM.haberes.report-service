package um.haberes.report.reports.comparacioncodigos.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.comparacioncodigos.domain.model.ComparacionCodigosContent;
import um.haberes.report.reports.comparacioncodigos.domain.ports.in.BuildComparacionCodigosContentUseCase;
import um.haberes.report.reports.comparacioncodigos.domain.ports.in.GenerateComparacionCodigosReportUseCase;
import um.haberes.report.reports.comparacioncodigos.domain.ports.out.ComparacionCodigosPdfRenderer;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class GenerateComparacionCodigosReportUseCaseImpl implements GenerateComparacionCodigosReportUseCase {

    static final String FILE_NAME = "comparacion_codigos.pdf";

    private final BuildComparacionCodigosContentUseCase buildContent;
    private final ComparacionCodigosPdfRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer anho, Integer mes) {
        ComparacionCodigosContent content = buildContent.build(anho, mes);
        Path target = storage.resolve(FILE_NAME);
        renderer.render(anho, mes, content, target);
        return storage.load(target, FILE_NAME, ReportFile.Format.PDF);
    }
}
