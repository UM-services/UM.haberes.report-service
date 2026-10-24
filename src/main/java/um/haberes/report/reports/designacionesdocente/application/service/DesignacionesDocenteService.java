package um.haberes.report.reports.designacionesdocente.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.haberes.report.reports.designacionesdocente.domain.ports.in.GenerateDesignacionesDocenteReportUseCase;
import um.haberes.report.shared.domain.model.ReportFile;

@Service
@RequiredArgsConstructor
public class DesignacionesDocenteService {

    private final GenerateDesignacionesDocenteReportUseCase generateReport;

    public ReportFile generate(Integer anho, Integer mes) {
        return generateReport.generate(anho, mes);
    }
}
