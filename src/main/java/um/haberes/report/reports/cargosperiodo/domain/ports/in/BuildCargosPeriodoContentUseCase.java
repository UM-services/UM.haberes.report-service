package um.haberes.report.reports.cargosperiodo.domain.ports.in;

import um.haberes.report.reports.cargosperiodo.domain.model.CargosPeriodoContent;

public interface BuildCargosPeriodoContentUseCase {

    CargosPeriodoContent build(Integer anho, Integer mes);
}
