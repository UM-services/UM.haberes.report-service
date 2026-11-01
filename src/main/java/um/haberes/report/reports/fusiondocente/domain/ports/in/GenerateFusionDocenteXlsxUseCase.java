package um.haberes.report.reports.fusiondocente.domain.ports.in;

import um.haberes.report.shared.domain.model.ReportFile;

/** Exporta a XLSX el mismo contenido que la tabla y el PDF del reporte. */
public interface GenerateFusionDocenteXlsxUseCase {

    ReportFile generate(Integer anho, Integer mes);
}
