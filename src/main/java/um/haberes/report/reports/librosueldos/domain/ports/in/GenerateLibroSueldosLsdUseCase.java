package um.haberes.report.reports.librosueldos.domain.ports.in;

import um.haberes.report.shared.domain.model.ReportFile;

import java.util.List;

/** ZIP del Libro de Sueldos Digital de AFIP para los legajos visibles. */
public interface GenerateLibroSueldosLsdUseCase {

    ReportFile generate(Integer anho, Integer mes, List<Long> legajoIds);
}
