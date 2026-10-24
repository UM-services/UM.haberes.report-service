package um.haberes.report.reports.cursodocente.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.haberes.report.reports.cursodocente.domain.ports.in.GenerateCursoDocenteReportUseCase;
import um.haberes.report.shared.domain.model.ReportFile;

@Service
@RequiredArgsConstructor
public class CursoDocenteService {

    private final GenerateCursoDocenteReportUseCase generateReport;

    public ReportFile generate(Integer anho, Integer mes) {
        return generateReport.generate(anho, mes);
    }
}
