package um.haberes.report.reports.totalescodigo.domain.ports.in;

import um.haberes.report.shared.domain.model.ReportFile;

public interface GenerateTotalesCodigoReportUseCase {
    ReportFile generate(Integer anho, Integer mes);
}
