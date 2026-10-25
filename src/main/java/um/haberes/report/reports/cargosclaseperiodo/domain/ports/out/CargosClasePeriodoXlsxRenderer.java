package um.haberes.report.reports.cargosclaseperiodo.domain.ports.out;

import um.haberes.report.reports.cargosclaseperiodo.domain.model.CargosClasePeriodoContent;

import java.nio.file.Path;

/** Exporta el mismo contenido del reporte a XLSX. */
public interface CargosClasePeriodoXlsxRenderer {

    void render(CargosClasePeriodoContent content, Path target);
}
