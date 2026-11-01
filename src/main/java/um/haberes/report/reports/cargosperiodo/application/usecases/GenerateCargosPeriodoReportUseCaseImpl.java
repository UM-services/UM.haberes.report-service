package um.haberes.report.reports.cargosperiodo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.cargosperiodo.domain.model.CargosPeriodoContent;
import um.haberes.report.reports.cargosperiodo.domain.ports.in.BuildCargosPeriodoContentUseCase;
import um.haberes.report.reports.cargosperiodo.domain.ports.in.GenerateCargosPeriodoReportUseCase;
import um.haberes.report.reports.cargosperiodo.domain.ports.out.CargosPeriodoPdfRenderer;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class GenerateCargosPeriodoReportUseCaseImpl implements GenerateCargosPeriodoReportUseCase {

    static final String DISPLAY_NAME = "cargosperiodo.pdf";

    private final BuildCargosPeriodoContentUseCase buildContent;
    private final CargosPeriodoPdfRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer anho, Integer mes) {
        CargosPeriodoContent content = buildContent.build(anho, mes);
        Path target = storage.resolve("cargosperiodo." + anho + "." + mes + ".pdf");
        renderer.render(content, target);
        return storage.load(target, DISPLAY_NAME, ReportFile.Format.PDF);
    }
}
