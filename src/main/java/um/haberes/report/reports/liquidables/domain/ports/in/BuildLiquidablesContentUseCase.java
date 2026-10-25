package um.haberes.report.reports.liquidables.domain.ports.in;

import um.haberes.report.reports.liquidables.domain.model.LiquidablesContent;

/** Arma una sola vez el contenido que consumen la tabla, el PDF y el XLSX. */
public interface BuildLiquidablesContentUseCase {

    LiquidablesContent build();
}
