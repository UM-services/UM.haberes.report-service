package um.haberes.report.reports.librosueldos.domain.ports.in;

import um.haberes.report.shared.domain.model.ReportFile;

public interface GenerateLibroSueldosReportUseCase {

    ReportFile generate(Integer anho, Integer mes);
}
