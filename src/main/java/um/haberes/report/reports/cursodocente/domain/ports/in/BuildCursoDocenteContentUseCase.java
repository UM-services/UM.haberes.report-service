package um.haberes.report.reports.cursodocente.domain.ports.in;

import um.haberes.report.reports.cursodocente.domain.model.CursoDocenteContent;

/** Contenido compartido por la tabla, el PDF y el XLSX del reporte. */
public interface BuildCursoDocenteContentUseCase {

    CursoDocenteContent build(Integer anho, Integer mes);
}
