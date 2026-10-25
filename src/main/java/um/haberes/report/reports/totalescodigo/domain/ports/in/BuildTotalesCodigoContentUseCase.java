package um.haberes.report.reports.totalescodigo.domain.ports.in;

import um.haberes.report.reports.totalescodigo.domain.model.TotalesCodigoContent;

/** Contenido compartido por la tabla, el PDF y el XLSX del reporte. */
public interface BuildTotalesCodigoContentUseCase {

    TotalesCodigoContent build(Integer anho, Integer mes);
}
