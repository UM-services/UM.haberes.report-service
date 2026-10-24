package um.haberes.report.reports.cursocargonovedad.domain.ports.in;

import um.haberes.report.shared.domain.model.ReportFile;

public interface GenerateCursocargoNovedadReportUseCase {

    ReportFile generate(Integer anho, Integer mes, boolean soloPendientes);
}
