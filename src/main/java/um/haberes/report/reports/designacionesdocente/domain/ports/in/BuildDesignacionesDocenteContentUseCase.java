package um.haberes.report.reports.designacionesdocente.domain.ports.in;

import um.haberes.report.reports.designacionesdocente.domain.model.DesignacionesDocenteContent;

/** Contenido compartido por la tabla, el PDF y el XLSX del reporte. */
public interface BuildDesignacionesDocenteContentUseCase {

    DesignacionesDocenteContent build(Integer anho, Integer mes);
}
