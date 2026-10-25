package um.haberes.report.reports.liquidables.domain.ports.in;

import um.haberes.report.shared.domain.model.ReportFile;

/** Genera el XLSX de la planilla de liquidables a partir del contenido compartido. */
public interface GenerateLiquidablesXlsxUseCase {

    ReportFile generate();
}
