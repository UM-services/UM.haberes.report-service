package um.haberes.report.reports.fusiondocente.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.haberes.report.reports.fusiondocente.domain.ports.in.GenerateFusionDocenteReportUseCase;
import um.haberes.report.shared.domain.model.ReportFile;

@Service
@RequiredArgsConstructor
public class FusionDocenteService {

    private final GenerateFusionDocenteReportUseCase generateReport;

    public ReportFile generate(Integer anho, Integer mes) {
        return generateReport.generate(anho, mes);
    }
}
