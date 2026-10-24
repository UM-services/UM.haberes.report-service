package um.haberes.report.reports.novedadesdocentes.domain.ports.in;

import um.haberes.report.shared.domain.model.ReportFile;

public interface GenerateNovedadesDocentesReportUseCase {

    ReportFile generate(Integer facultadId, Integer anho, Integer mes);
}
