package um.haberes.report.reports.totalesgenerales.domain.ports.out;

import um.haberes.report.reports.totalesgenerales.domain.model.TotalesGeneralesContent;

import java.nio.file.Path;

/** Exporta el mismo contenido del reporte a XLSX. */
public interface TotalesGeneralesXlsxRenderer {

    void render(TotalesGeneralesContent content, Path target);
}
