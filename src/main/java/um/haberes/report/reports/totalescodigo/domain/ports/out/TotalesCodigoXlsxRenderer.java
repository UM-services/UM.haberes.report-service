package um.haberes.report.reports.totalescodigo.domain.ports.out;

import um.haberes.report.reports.totalescodigo.domain.model.TotalesCodigoContent;

import java.nio.file.Path;

/** Exporta el mismo contenido del reporte a XLSX. */
public interface TotalesCodigoXlsxRenderer {

    void render(Integer anho, Integer mes, TotalesCodigoContent content, Path target);
}
