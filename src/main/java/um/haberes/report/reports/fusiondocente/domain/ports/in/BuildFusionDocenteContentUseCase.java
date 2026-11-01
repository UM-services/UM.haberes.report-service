package um.haberes.report.reports.fusiondocente.domain.ports.in;

import um.haberes.report.reports.fusiondocente.domain.model.FusionDocenteContent;

/** Contenido compartido por la tabla, el PDF y el XLSX del reporte. */
public interface BuildFusionDocenteContentUseCase {

    FusionDocenteContent build(Integer anho, Integer mes);
}
