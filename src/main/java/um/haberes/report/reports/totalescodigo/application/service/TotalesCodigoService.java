package um.haberes.report.reports.totalescodigo.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.haberes.report.reports.totalescodigo.domain.ports.in.GenerateTotalesCodigoReportUseCase;
import um.haberes.report.shared.domain.model.ReportFile;

@Service
@RequiredArgsConstructor
public class TotalesCodigoService {

    private final GenerateTotalesCodigoReportUseCase generateReport;

    public ReportFile generate(Integer anho, Integer mes) {
        return generateReport.generate(anho, mes);
    }
}
