package um.haberes.report.reports.cantidadcargos.domain.ports.in;

import um.haberes.report.shared.domain.model.ReportFile;

public interface GenerateCantidadCargosXlsxUseCase {

    ReportFile generate(Integer anho, Integer mes);
}
