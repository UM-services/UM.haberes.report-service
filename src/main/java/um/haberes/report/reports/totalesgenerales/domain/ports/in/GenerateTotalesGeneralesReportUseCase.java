package um.haberes.report.reports.totalesgenerales.domain.ports.in;

import um.haberes.report.shared.domain.model.ReportFile;

public interface GenerateTotalesGeneralesReportUseCase {

    ReportFile generate(Integer anho, Integer mes);
}
