package um.haberes.report.reports.comparacioncodigos.domain.ports.in;

import um.haberes.report.reports.comparacioncodigos.domain.model.ComparacionCodigosContent;

/** Contenido compartido por la tabla, el PDF y el XLSX del reporte. */
public interface BuildComparacionCodigosContentUseCase {

    ComparacionCodigosContent build(Integer anho, Integer mes);
}
