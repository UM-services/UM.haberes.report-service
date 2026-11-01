package um.haberes.report.reports.anotador.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.haberes.report.reports.anotador.domain.ports.in.GenerateAnotadorReportUseCase;
import um.haberes.report.shared.domain.model.ReportFile;

@Service
@RequiredArgsConstructor
public class AnotadorService {

    private final GenerateAnotadorReportUseCase generateReport;

    public ReportFile generate(Integer facultadId, Integer anho, Integer mes, boolean pendientes) {
        return generateReport.generate(facultadId, anho, mes, pendientes);
    }
}
