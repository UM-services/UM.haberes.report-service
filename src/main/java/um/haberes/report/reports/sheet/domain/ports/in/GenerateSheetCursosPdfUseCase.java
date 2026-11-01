package um.haberes.report.reports.sheet.domain.ports.in;

import um.haberes.report.shared.domain.model.ReportFile;

public interface GenerateSheetCursosPdfUseCase {

    ReportFile generate(Integer anho, Integer mes);
}
