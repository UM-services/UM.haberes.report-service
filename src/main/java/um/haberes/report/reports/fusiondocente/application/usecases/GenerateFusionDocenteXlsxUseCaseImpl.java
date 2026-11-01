package um.haberes.report.reports.fusiondocente.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.fusiondocente.domain.model.FusionDocenteContent;
import um.haberes.report.reports.fusiondocente.domain.ports.in.BuildFusionDocenteContentUseCase;
import um.haberes.report.reports.fusiondocente.domain.ports.in.GenerateFusionDocenteXlsxUseCase;
import um.haberes.report.reports.fusiondocente.domain.ports.out.FusionDocenteXlsxRenderer;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class GenerateFusionDocenteXlsxUseCaseImpl implements GenerateFusionDocenteXlsxUseCase {

    static final String DISPLAY_NAME = "fusion.docente.xlsx";

    private final BuildFusionDocenteContentUseCase buildContent;
    private final FusionDocenteXlsxRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer anho, Integer mes) {
        FusionDocenteContent content = buildContent.build(anho, mes);
        Path target = storage.resolve("fusion.docente." + anho + "." + mes + ".xlsx");
        renderer.render(content, target);
        return storage.load(target, DISPLAY_NAME, ReportFile.Format.XLSX);
    }
}
