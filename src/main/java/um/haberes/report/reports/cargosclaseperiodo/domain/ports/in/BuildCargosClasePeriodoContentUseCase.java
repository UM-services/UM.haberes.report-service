package um.haberes.report.reports.cargosclaseperiodo.domain.ports.in;

import um.haberes.report.reports.cargosclaseperiodo.domain.model.CargosClasePeriodoContent;

/** Contenido compartido por la tabla, el PDF y el XLSX del reporte. */
public interface BuildCargosClasePeriodoContentUseCase {

    CargosClasePeriodoContent build(Integer facultadId, Integer anho, Integer mes);
}
