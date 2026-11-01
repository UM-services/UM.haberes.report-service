package um.haberes.report.reports.docentessede.domain.ports.out;

import um.haberes.report.reports.docentessede.domain.model.DocentesSedeContent;

import java.nio.file.Path;

/** Exporta el mismo contenido del reporte a XLSX. */
public interface DocentesSedeXlsxRenderer {

    void render(DocentesSedeContent content, Path target);
}
