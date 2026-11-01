package um.haberes.report.reports.cargosclaseperiodo.domain.ports.out;

import um.haberes.report.reports.cargosclaseperiodo.domain.model.CargosClasePeriodoContent;

import java.nio.file.Path;

public interface CargosClasePeriodoPdfRenderer {

    void render(CargosClasePeriodoContent content, Path target);
}
