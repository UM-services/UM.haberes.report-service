package um.haberes.report.reports.comparacioncodigos.domain.ports.in;

import um.haberes.report.shared.domain.model.ReportFile;

public interface GenerateComparacionCodigosReportUseCase {

    ReportFile generate(Integer anho, Integer mes);
}
