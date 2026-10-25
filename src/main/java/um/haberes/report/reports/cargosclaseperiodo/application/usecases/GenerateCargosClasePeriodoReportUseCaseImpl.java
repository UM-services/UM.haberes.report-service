package um.haberes.report.reports.cargosclaseperiodo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.cargosclaseperiodo.domain.model.CargosClasePeriodoContent;
import um.haberes.report.reports.cargosclaseperiodo.domain.ports.in.BuildCargosClasePeriodoContentUseCase;
import um.haberes.report.reports.cargosclaseperiodo.domain.ports.in.GenerateCargosClasePeriodoReportUseCase;
import um.haberes.report.reports.cargosclaseperiodo.domain.ports.out.CargosClasePeriodoPdfRenderer;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class GenerateCargosClasePeriodoReportUseCaseImpl
        implements GenerateCargosClasePeriodoReportUseCase {

    static final String DISPLAY_NAME = "cargos-clase-periodo.pdf";

    private final BuildCargosClasePeriodoContentUseCase buildContent;
    private final CargosClasePeriodoPdfRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer facultadId, Integer anho, Integer mes) {
        CargosClasePeriodoContent content = buildContent.build(facultadId, anho, mes);
        Path target = storage.resolve(fileName(facultadId, anho, mes));
        renderer.render(content, target);
        return storage.load(target, DISPLAY_NAME, ReportFile.Format.PDF);
    }

    private String fileName(Integer facultadId, Integer anho, Integer mes) {
        return "cargos-clase-periodo.." + facultadId + "." + anho + mes + "..pdf";
    }
}
