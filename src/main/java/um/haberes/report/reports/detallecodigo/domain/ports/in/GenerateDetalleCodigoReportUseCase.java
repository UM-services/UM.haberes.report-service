package um.haberes.report.reports.detallecodigo.domain.ports.in;

import um.haberes.report.shared.domain.model.ReportFile;

public interface GenerateDetalleCodigoReportUseCase {

    ReportFile generate(Integer codigoId, Integer anho, Integer mes);
}
