package um.haberes.report.reports.librosueldos.domain.ports.in;

import um.haberes.report.reports.librosueldos.domain.model.LibroSueldosContent;

public interface BuildLibroSueldosContentUseCase {

    LibroSueldosContent build(Integer anho, Integer mes);
}
