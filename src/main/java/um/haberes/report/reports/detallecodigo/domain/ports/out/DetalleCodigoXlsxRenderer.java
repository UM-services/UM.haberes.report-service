package um.haberes.report.reports.detallecodigo.domain.ports.out;

import um.haberes.report.reports.detallecodigo.domain.model.DetalleCodigoContent;

import java.nio.file.Path;

public interface DetalleCodigoXlsxRenderer {

    void render(DetalleCodigoContent content, Path target);
}
