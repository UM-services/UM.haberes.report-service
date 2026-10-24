package um.haberes.report.reports.anotador.domain.ports.in;

import um.haberes.report.shared.domain.model.ReportFile;

public interface GenerateAnotadorReportUseCase {

    /** @param facultadId {@code null} para la variante consolidada de todo el periodo. */
    ReportFile generate(Integer facultadId, Integer anho, Integer mes, boolean pendientes);
}
