package um.haberes.report.reports.comparacioncodigos.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.haberes.report.reports.comparacioncodigos.domain.ports.in.GenerateComparacionCodigosReportUseCase;
import um.haberes.report.shared.domain.model.ReportFile;

@Service
@RequiredArgsConstructor
public class ComparacionCodigosService {

    private final GenerateComparacionCodigosReportUseCase generateReport;

    public ReportFile generate(Integer anho, Integer mes) {
        return generateReport.generate(anho, mes);
    }
}
