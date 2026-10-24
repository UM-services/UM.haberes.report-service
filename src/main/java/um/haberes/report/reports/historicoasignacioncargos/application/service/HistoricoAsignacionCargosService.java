package um.haberes.report.reports.historicoasignacioncargos.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.haberes.report.reports.historicoasignacioncargos.domain.ports.in.GenerateHistoricoAsignacionCargosReportUseCase;
import um.haberes.report.shared.domain.model.ReportFile;

@Service
@RequiredArgsConstructor
public class HistoricoAsignacionCargosService {

    private final GenerateHistoricoAsignacionCargosReportUseCase generateReport;

    public ReportFile generate(Integer categoriaId) {
        return generateReport.generate(categoriaId);
    }
}
