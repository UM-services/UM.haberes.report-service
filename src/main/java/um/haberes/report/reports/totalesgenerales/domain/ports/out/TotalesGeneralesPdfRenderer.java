package um.haberes.report.reports.totalesgenerales.domain.ports.out;

import um.haberes.report.reports.totalesgenerales.domain.model.TotalesGeneralesContent;

import java.nio.file.Path;

public interface TotalesGeneralesPdfRenderer {

    void render(TotalesGeneralesContent content, Path target);
}
