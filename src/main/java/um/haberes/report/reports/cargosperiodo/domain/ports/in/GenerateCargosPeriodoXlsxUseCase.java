package um.haberes.report.reports.cargosperiodo.domain.ports.in;

import um.haberes.report.shared.domain.model.ReportFile;

public interface GenerateCargosPeriodoXlsxUseCase {

    ReportFile generate(Integer anho, Integer mes);
}
