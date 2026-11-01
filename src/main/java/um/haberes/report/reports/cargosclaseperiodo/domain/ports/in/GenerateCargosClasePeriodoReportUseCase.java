package um.haberes.report.reports.cargosclaseperiodo.domain.ports.in;

import um.haberes.report.shared.domain.model.ReportFile;

public interface GenerateCargosClasePeriodoReportUseCase {

    ReportFile generate(Integer facultadId, Integer anho, Integer mes);
}
