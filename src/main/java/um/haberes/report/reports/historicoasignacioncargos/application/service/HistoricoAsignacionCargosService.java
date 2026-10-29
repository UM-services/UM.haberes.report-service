package um.haberes.report.reports.historicoasignacioncargos.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.haberes.report.reports.historicoasignacioncargos.domain.model.HistoricoAsignacionCargosContent;
import um.haberes.report.reports.historicoasignacioncargos.domain.ports.in.BuildHistoricoAsignacionCargosContentUseCase;
import um.haberes.report.reports.historicoasignacioncargos.domain.ports.in.GenerateHistoricoAsignacionCargosReportUseCase;
import um.haberes.report.shared.domain.model.ReportFile;

@Service
@RequiredArgsConstructor
public class HistoricoAsignacionCargosService {

    private final BuildHistoricoAsignacionCargosContentUseCase buildContent;
    private final GenerateHistoricoAsignacionCargosReportUseCase generatePdf;

    public HistoricoAsignacionCargosContent content(Integer categoriaId) {
        return buildContent.build(categoriaId);
    }

    public ReportFile generate(Integer categoriaId) {
        return generatePdf.generate(categoriaId);
    }
}
