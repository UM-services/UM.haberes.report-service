package um.haberes.report.reports.comparacionremuneraciones.domain.ports.in;

import um.haberes.report.reports.comparacionremuneraciones.domain.model.ComparacionRemuneracionesContent;

/** Contenido compartido por la tabla, el PDF y el XLSX del reporte. */
public interface BuildComparacionRemuneracionesContentUseCase {

    ComparacionRemuneracionesContent build(Integer anho, Integer mes, Integer anhoAnterior,
                                           Integer mesAnterior);
}
