package um.haberes.report.reports.fusiondocente.domain.ports.in;

import um.haberes.report.shared.domain.model.ReportFile;

public interface GenerateFusionDocenteReportUseCase {

    ReportFile generate(Integer anho, Integer mes);
}
