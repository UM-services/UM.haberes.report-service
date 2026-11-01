package um.haberes.report.reports.comparacionremuneraciones.domain.ports.out;

import um.haberes.report.reports.comparacionremuneraciones.domain.model.ComparacionRemuneracionesContent;

import java.nio.file.Path;

/** Exporta el mismo contenido del reporte a XLSX. */
public interface ComparacionRemuneracionesXlsxRenderer {

    void render(ComparacionRemuneracionesContent content, Path target);
}
