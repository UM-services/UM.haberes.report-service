package um.haberes.report.reports.docentessede.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.haberes.report.reports.docentessede.domain.ports.in.GenerateDocentesSedeReportUseCase;
import um.haberes.report.shared.domain.model.ReportFile;

@Service
@RequiredArgsConstructor
public class DocentesSedeService {

    private final GenerateDocentesSedeReportUseCase generateReport;

    public ReportFile generate(Integer facultadId, Integer geograficaId, Integer anho, Integer mes) {
        return generateReport.generate(facultadId, geograficaId, anho, mes);
    }
}
