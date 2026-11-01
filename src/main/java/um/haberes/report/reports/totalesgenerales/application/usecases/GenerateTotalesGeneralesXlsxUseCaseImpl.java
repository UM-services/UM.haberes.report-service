package um.haberes.report.reports.totalesgenerales.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.totalesgenerales.domain.model.TotalesGeneralesContent;
import um.haberes.report.reports.totalesgenerales.domain.ports.in.BuildTotalesGeneralesContentUseCase;
import um.haberes.report.reports.totalesgenerales.domain.ports.in.GenerateTotalesGeneralesXlsxUseCase;
import um.haberes.report.reports.totalesgenerales.domain.ports.out.TotalesGeneralesXlsxRenderer;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class GenerateTotalesGeneralesXlsxUseCaseImpl implements GenerateTotalesGeneralesXlsxUseCase {

    static final String DISPLAY_NAME = "totales_generales.xlsx";

    private final BuildTotalesGeneralesContentUseCase buildContent;
    private final TotalesGeneralesXlsxRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer anho, Integer mes) {
        TotalesGeneralesContent content = buildContent.build(anho, mes);
        Path target = storage.resolve(fileName(anho, mes));
        renderer.render(content, target);
        return storage.load(target, DISPLAY_NAME, ReportFile.Format.XLSX);
    }

    private String fileName(Integer anho, Integer mes) {
        return "totales.generales." + anho + "." + mes + ".xlsx";
    }
}
