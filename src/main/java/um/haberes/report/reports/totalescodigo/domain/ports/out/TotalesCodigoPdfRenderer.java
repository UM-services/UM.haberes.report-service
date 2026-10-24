package um.haberes.report.reports.totalescodigo.domain.ports.out;

import um.haberes.report.reports.totalescodigo.domain.model.TotalesCodigoContent;

import java.nio.file.Path;

public interface TotalesCodigoPdfRenderer {
    void render(Integer anho, Integer mes, TotalesCodigoContent content, Path target);
}
