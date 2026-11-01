package um.haberes.report.reports.basicosperiodo.domain.ports.in;

import um.haberes.report.reports.basicosperiodo.domain.model.BasicosPeriodoContent;

public interface BuildBasicosPeriodoContentUseCase {

    BasicosPeriodoContent build(Integer anho, Integer mes);
}
