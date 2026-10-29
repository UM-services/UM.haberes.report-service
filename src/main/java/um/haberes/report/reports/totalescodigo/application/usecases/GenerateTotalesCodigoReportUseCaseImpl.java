package um.haberes.report.reports.totalescodigo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.totalescodigo.domain.model.TotalesCodigoContent;
import um.haberes.report.reports.totalescodigo.domain.ports.in.BuildTotalesCodigoContentUseCase;
import um.haberes.report.reports.totalescodigo.domain.ports.in.GenerateTotalesCodigoReportUseCase;
import um.haberes.report.reports.totalescodigo.domain.ports.out.TotalesCodigoPdfRenderer;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class GenerateTotalesCodigoReportUseCaseImpl implements GenerateTotalesCodigoReportUseCase {

    static final String FILE_NAME = "totales_codigo.pdf";

    private final BuildTotalesCodigoContentUseCase buildContent;
    private final TotalesCodigoPdfRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer anho, Integer mes) {
        TotalesCodigoContent content = buildContent.build(anho, mes);
        Path target = storage.resolve(FILE_NAME);
        renderer.render(anho, mes, content, target);
        return storage.load(target, FILE_NAME, ReportFile.Format.PDF);
    }
}
