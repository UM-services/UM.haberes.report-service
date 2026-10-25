package um.haberes.report.reports.cargosclaseperiodo.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.haberes.report.reports.cargosclaseperiodo.domain.model.CargosClasePeriodoContent;
import um.haberes.report.reports.cargosclaseperiodo.domain.ports.in.BuildCargosClasePeriodoContentUseCase;
import um.haberes.report.reports.cargosclaseperiodo.domain.ports.in.GenerateCargosClasePeriodoReportUseCase;
import um.haberes.report.reports.cargosclaseperiodo.domain.ports.in.GenerateCargosClasePeriodoXlsxUseCase;
import um.haberes.report.shared.domain.model.ReportFile;

@Service
@RequiredArgsConstructor
public class CargosClasePeriodoService {

    private final BuildCargosClasePeriodoContentUseCase buildContent;
    private final GenerateCargosClasePeriodoReportUseCase generatePdf;
    private final GenerateCargosClasePeriodoXlsxUseCase generateXlsx;

    public CargosClasePeriodoContent content(Integer facultadId, Integer anho, Integer mes) {
        return buildContent.build(facultadId, anho, mes);
    }

    public ReportFile pdf(Integer facultadId, Integer anho, Integer mes) {
        return generatePdf.generate(facultadId, anho, mes);
    }

    public ReportFile xlsx(Integer facultadId, Integer anho, Integer mes) {
        return generateXlsx.generate(facultadId, anho, mes);
    }
}
