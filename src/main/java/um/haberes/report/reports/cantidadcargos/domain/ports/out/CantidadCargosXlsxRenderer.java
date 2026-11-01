package um.haberes.report.reports.cantidadcargos.domain.ports.out;

import um.haberes.report.reports.cantidadcargos.domain.model.CantidadCargosContent;

import java.nio.file.Path;

public interface CantidadCargosXlsxRenderer {

    void render(CantidadCargosContent content, Path target);
}
