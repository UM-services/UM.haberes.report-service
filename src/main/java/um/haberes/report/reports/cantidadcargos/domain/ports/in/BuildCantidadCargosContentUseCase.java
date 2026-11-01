package um.haberes.report.reports.cantidadcargos.domain.ports.in;

import um.haberes.report.reports.cantidadcargos.domain.model.CantidadCargosContent;

public interface BuildCantidadCargosContentUseCase {

    CantidadCargosContent build(Integer anho, Integer mes);
}
