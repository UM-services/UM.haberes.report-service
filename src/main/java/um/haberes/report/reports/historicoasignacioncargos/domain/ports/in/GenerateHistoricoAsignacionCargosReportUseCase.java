package um.haberes.report.reports.historicoasignacioncargos.domain.ports.in;

import um.haberes.report.shared.domain.model.ReportFile;

public interface GenerateHistoricoAsignacionCargosReportUseCase {

    ReportFile generate(Integer categoriaId);
}
