package um.haberes.report.reports.bono.domain.ports.out;

import um.haberes.report.reports.bono.domain.model.DetalleCargosContent;

import java.nio.file.Path;

public interface DetalleCargosPdfRenderer {

    void render(DetalleCargosContent content, Path target);
}
