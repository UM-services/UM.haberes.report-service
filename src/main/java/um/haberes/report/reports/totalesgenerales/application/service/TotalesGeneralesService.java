package um.haberes.report.reports.totalesgenerales.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.haberes.report.reports.totalesgenerales.domain.ports.in.GenerateTotalesGeneralesReportUseCase;
import um.haberes.report.shared.domain.model.ReportFile;

@Service
@RequiredArgsConstructor
public class TotalesGeneralesService {

    private final GenerateTotalesGeneralesReportUseCase generateReport;

    public ReportFile generate(Integer anho, Integer mes) {
        return generateReport.generate(anho, mes);
    }
}
