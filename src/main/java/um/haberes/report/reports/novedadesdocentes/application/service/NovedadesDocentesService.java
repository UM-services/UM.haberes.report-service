package um.haberes.report.reports.novedadesdocentes.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.haberes.report.reports.novedadesdocentes.domain.ports.in.GenerateNovedadesDocentesReportUseCase;
import um.haberes.report.shared.domain.model.ReportFile;

@Service
@RequiredArgsConstructor
public class NovedadesDocentesService {

    private final GenerateNovedadesDocentesReportUseCase generateReport;

    public ReportFile generate(Integer facultadId, Integer anho, Integer mes) {
        return generateReport.generate(facultadId, anho, mes);
    }
}
