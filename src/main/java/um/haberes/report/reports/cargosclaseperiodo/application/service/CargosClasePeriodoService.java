package um.haberes.report.reports.cargosclaseperiodo.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.haberes.report.reports.cargosclaseperiodo.domain.ports.in.GenerateCargosClasePeriodoReportUseCase;
import um.haberes.report.shared.domain.model.ReportFile;

@Service
@RequiredArgsConstructor
public class CargosClasePeriodoService {

    private final GenerateCargosClasePeriodoReportUseCase generateReport;

    public ReportFile generate(Integer facultadId, Integer anho, Integer mes) {
        return generateReport.generate(facultadId, anho, mes);
    }
}
