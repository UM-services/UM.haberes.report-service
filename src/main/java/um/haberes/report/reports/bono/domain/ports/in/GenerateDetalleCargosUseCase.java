package um.haberes.report.reports.bono.domain.ports.in;

import um.haberes.report.shared.domain.model.ReportFile;

public interface GenerateDetalleCargosUseCase {

    ReportFile generar(Long legajoId, Integer anho, Integer mes, Integer facultadId);
}
