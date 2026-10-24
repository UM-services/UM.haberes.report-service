package um.haberes.report.reports.docentessede.domain.ports.in;

import um.haberes.report.shared.domain.model.ReportFile;

public interface GenerateDocentesSedeReportUseCase {

    ReportFile generate(Integer facultadId, Integer geograficaId, Integer anho, Integer mes);
}
