package um.haberes.report.reports.sheet.domain.ports.in;

import um.haberes.report.reports.sheet.domain.model.SheetCursosContent;

/** Arma el contenido de la hoja de cursos, compartido por el XLSX legacy y el PDF nuevo. */
public interface BuildSheetCursosContentUseCase {

    SheetCursosContent build(Integer anho, Integer mes);
}
