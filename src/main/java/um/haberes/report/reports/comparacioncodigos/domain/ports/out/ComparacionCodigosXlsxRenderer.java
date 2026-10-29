package um.haberes.report.reports.comparacioncodigos.domain.ports.out;

import um.haberes.report.reports.comparacioncodigos.domain.model.ComparacionCodigosContent;

import java.nio.file.Path;

/** Exporta el mismo contenido del reporte a XLSX. */
public interface ComparacionCodigosXlsxRenderer {

    void render(Integer anho, Integer mes, ComparacionCodigosContent content, Path target);
}
