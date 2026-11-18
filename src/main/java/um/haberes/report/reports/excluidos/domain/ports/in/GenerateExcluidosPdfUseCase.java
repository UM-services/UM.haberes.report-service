package um.haberes.report.reports.excluidos.domain.ports.in;

import um.haberes.report.shared.domain.model.ReportFile;

public interface GenerateExcluidosPdfUseCase {

    ReportFile generate(Integer anho, Integer mes);
}
