package um.haberes.report.reports.docentessede.domain.ports.in;

import um.haberes.report.reports.docentessede.domain.model.DocentesSedeContent;

/** Contenido compartido por la tabla, el PDF y el XLSX del reporte. */
public interface BuildDocentesSedeContentUseCase {

    DocentesSedeContent build(Integer facultadId, Integer geograficaId, Integer anho, Integer mes);
}
