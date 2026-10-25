package um.haberes.report.reports.cargosperiodo.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.haberes.report.reports.cargosperiodo.domain.model.CargosPeriodoContent;
import um.haberes.report.reports.cargosperiodo.domain.ports.in.BuildCargosPeriodoContentUseCase;
import um.haberes.report.reports.cargosperiodo.domain.ports.in.GenerateCargosPeriodoReportUseCase;
import um.haberes.report.reports.cargosperiodo.domain.ports.in.GenerateCargosPeriodoXlsxUseCase;
import um.haberes.report.shared.domain.model.ReportFile;

@Service
@RequiredArgsConstructor
public class CargosPeriodoService {

    private final BuildCargosPeriodoContentUseCase buildContent;
    private final GenerateCargosPeriodoReportUseCase generatePdf;
    private final GenerateCargosPeriodoXlsxUseCase generateXlsx;

    public CargosPeriodoContent content(Integer anho, Integer mes) {
        return buildContent.build(anho, mes);
    }

    public ReportFile pdf(Integer anho, Integer mes) {
        return generatePdf.generate(anho, mes);
    }

    public ReportFile xlsx(Integer anho, Integer mes) {
        return generateXlsx.generate(anho, mes);
    }
}
