package um.haberes.report.reports.designacionesdetalle.domain.ports.in;

import um.haberes.report.shared.domain.model.ReportFile;

public interface GenerateDesignacionesDetalleReportUseCase {

    ReportFile generate(Long legajoId, Integer anho, Integer mes);
}
