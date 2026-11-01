package um.haberes.report.reports.designacionesdetalle.domain.ports.out;

import um.haberes.report.reports.designacionesdetalle.domain.model.DesignacionesDetalleContent;

import java.nio.file.Path;

public interface DesignacionesDetallePdfRenderer {

    void render(DesignacionesDetalleContent content, Path target);
}
