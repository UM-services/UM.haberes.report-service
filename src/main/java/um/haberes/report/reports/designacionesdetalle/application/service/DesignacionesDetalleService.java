package um.haberes.report.reports.designacionesdetalle.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.haberes.report.reports.designacionesdetalle.domain.ports.in.GenerateDesignacionesDetalleReportUseCase;
import um.haberes.report.shared.domain.model.ReportFile;

@Service
@RequiredArgsConstructor
public class DesignacionesDetalleService {

    private final GenerateDesignacionesDetalleReportUseCase generateReport;

    public ReportFile generate(Long legajoId, Integer anho, Integer mes) {
        return generateReport.generate(legajoId, anho, mes);
    }
}
