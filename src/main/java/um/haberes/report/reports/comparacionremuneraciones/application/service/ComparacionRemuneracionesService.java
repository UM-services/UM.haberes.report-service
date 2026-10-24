package um.haberes.report.reports.comparacionremuneraciones.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.haberes.report.reports.comparacionremuneraciones.domain.ports.in.GenerateComparacionRemuneracionesReportUseCase;
import um.haberes.report.shared.domain.model.ReportFile;

@Service
@RequiredArgsConstructor
public class ComparacionRemuneracionesService {

    private final GenerateComparacionRemuneracionesReportUseCase generateReport;

    public ReportFile generate(Integer anho, Integer mes, Integer anhoAnterior, Integer mesAnterior) {
        return generateReport.generate(anho, mes, anhoAnterior, mesAnterior);
    }
}
