package um.haberes.report.reports.cargosclase.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.haberes.report.reports.cargosclase.domain.ports.in.GenerateCargosClaseReportUseCase;
import um.haberes.report.shared.domain.model.ReportFile;

@Service
@RequiredArgsConstructor
public class CargosClaseService {

    private final GenerateCargosClaseReportUseCase generateCargosClaseReport;

    public ReportFile generate() {
        return generateCargosClaseReport.generate();
    }
}
