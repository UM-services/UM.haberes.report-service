package um.haberes.report.reports.totalescodigo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.totalescodigo.domain.model.TotalesCodigoContent;
import um.haberes.report.reports.totalescodigo.domain.ports.in.BuildTotalesCodigoContentUseCase;
import um.haberes.report.reports.totalescodigo.domain.ports.in.GenerateTotalesCodigoXlsxUseCase;
import um.haberes.report.reports.totalescodigo.domain.ports.out.TotalesCodigoXlsxRenderer;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class GenerateTotalesCodigoXlsxUseCaseImpl implements GenerateTotalesCodigoXlsxUseCase {

    static final String FILE_NAME = "totales_codigo.xlsx";

    private final BuildTotalesCodigoContentUseCase buildContent;
    private final TotalesCodigoXlsxRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer anho, Integer mes) {
        TotalesCodigoContent content = buildContent.build(anho, mes);
        Path target = storage.resolve("totales.codigo." + anho + "." + mes + ".xlsx");
        renderer.render(anho, mes, content, target);
        return storage.load(target, FILE_NAME, ReportFile.Format.XLSX);
    }
}
