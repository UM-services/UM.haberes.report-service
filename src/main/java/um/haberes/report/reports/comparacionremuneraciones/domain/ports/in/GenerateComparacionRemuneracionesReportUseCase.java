package um.haberes.report.reports.comparacionremuneraciones.domain.ports.in;

import um.haberes.report.shared.domain.model.ReportFile;

public interface GenerateComparacionRemuneracionesReportUseCase {

    ReportFile generate(Integer anho, Integer mes, Integer anhoAnterior, Integer mesAnterior);
}
