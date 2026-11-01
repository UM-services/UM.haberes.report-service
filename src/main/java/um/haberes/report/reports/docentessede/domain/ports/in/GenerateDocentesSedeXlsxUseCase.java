package um.haberes.report.reports.docentessede.domain.ports.in;

import um.haberes.report.shared.domain.model.ReportFile;

/** Exporta a XLSX el mismo contenido que la tabla y el PDF del reporte. */
public interface GenerateDocentesSedeXlsxUseCase {

    ReportFile generate(Integer facultadId, Integer geograficaId, Integer anho, Integer mes);
}
