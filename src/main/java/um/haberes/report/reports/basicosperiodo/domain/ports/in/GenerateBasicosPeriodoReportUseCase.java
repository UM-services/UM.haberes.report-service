package um.haberes.report.reports.basicosperiodo.domain.ports.in;

import um.haberes.report.shared.domain.model.ReportFile;

public interface GenerateBasicosPeriodoReportUseCase {

    ReportFile generate(Integer anho, Integer mes);
}
