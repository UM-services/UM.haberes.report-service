package um.haberes.report.reports.cursodocente.domain.ports.in;

import um.haberes.report.shared.domain.model.ReportFile;

public interface GenerateCursoDocenteReportUseCase {

    ReportFile generate(Integer anho, Integer mes);
}
