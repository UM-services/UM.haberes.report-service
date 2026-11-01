package um.haberes.report.reports.historicoasignacioncargos.domain.ports.in;

import um.haberes.report.reports.historicoasignacioncargos.domain.model.HistoricoAsignacionCargosContent;

/**
 * Contenido del histórico de asignación de cargos. El reporte original sólo emite encabezado,
 * así que el contenido se limita a la categoría elegida: no hay filas que calcular.
 */
public interface BuildHistoricoAsignacionCargosContentUseCase {

    HistoricoAsignacionCargosContent build(Integer categoriaId);
}
