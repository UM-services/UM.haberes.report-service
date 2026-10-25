package um.haberes.report.reports.totalesgenerales.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.totalesgenerales.domain.model.TotalesGeneralesContent;
import um.haberes.report.reports.totalesgenerales.domain.ports.in.BuildTotalesGeneralesContentUseCase;
import um.haberes.report.reports.totalesgenerales.domain.ports.in.GenerateTotalesGeneralesReportUseCase;
import um.haberes.report.reports.totalesgenerales.domain.ports.out.TotalesGeneralesPdfRenderer;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class GenerateTotalesGeneralesReportUseCaseImpl
        implements GenerateTotalesGeneralesReportUseCase {

    static final String DISPLAY_NAME = "totales_generales.pdf";

    private final BuildTotalesGeneralesContentUseCase buildContent;
    private final TotalesGeneralesPdfRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer anho, Integer mes) {
        TotalesGeneralesContent content = buildContent.build(anho, mes);
        Path target = storage.resolve(fileName(anho, mes));
        renderer.render(content, target);
        return storage.load(target, DISPLAY_NAME, ReportFile.Format.PDF);
    }

    private String fileName(Integer anho, Integer mes) {
        return "totales.generales.." + anho + "." + mes + ".pdf";
    }
}
