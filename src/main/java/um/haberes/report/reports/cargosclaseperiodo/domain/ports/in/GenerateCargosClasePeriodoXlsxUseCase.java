package um.haberes.report.reports.cargosclaseperiodo.domain.ports.in;

import um.haberes.report.shared.domain.model.ReportFile;

/** Exporta a XLSX el mismo contenido que la tabla y el PDF del reporte. */
public interface GenerateCargosClasePeriodoXlsxUseCase {

    ReportFile generate(Integer facultadId, Integer anho, Integer mes);
}
