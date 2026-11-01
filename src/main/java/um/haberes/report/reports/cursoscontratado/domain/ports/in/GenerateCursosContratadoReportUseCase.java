package um.haberes.report.reports.cursoscontratado.domain.ports.in;

import um.haberes.report.shared.domain.model.ReportFile;

public interface GenerateCursosContratadoReportUseCase {

    ReportFile generate(Integer anho, Integer mes);
}
