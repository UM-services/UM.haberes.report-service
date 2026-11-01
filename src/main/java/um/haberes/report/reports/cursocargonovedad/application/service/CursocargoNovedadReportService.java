package um.haberes.report.reports.cursocargonovedad.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.haberes.report.reports.cursocargonovedad.domain.ports.in.GenerateCursocargoNovedadReportUseCase;
import um.haberes.report.shared.domain.model.ReportFile;

@Service
@RequiredArgsConstructor
public class CursocargoNovedadReportService {

    private final GenerateCursocargoNovedadReportUseCase generateReport;

    public ReportFile generate(Integer anho, Integer mes, boolean soloPendientes) {
        return generateReport.generate(anho, mes, soloPendientes);
    }
}
