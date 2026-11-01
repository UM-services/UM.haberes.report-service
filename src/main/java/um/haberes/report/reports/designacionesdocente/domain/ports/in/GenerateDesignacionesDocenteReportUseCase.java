package um.haberes.report.reports.designacionesdocente.domain.ports.in;

import um.haberes.report.shared.domain.model.ReportFile;

public interface GenerateDesignacionesDocenteReportUseCase {

    ReportFile generate(Integer anho, Integer mes);
}
