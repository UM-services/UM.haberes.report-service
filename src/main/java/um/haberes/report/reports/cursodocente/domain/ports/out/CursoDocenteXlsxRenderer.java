package um.haberes.report.reports.cursodocente.domain.ports.out;

import um.haberes.report.reports.cursodocente.domain.model.CursoDocenteContent;

import java.nio.file.Path;

/** Exporta el mismo contenido del reporte a XLSX. */
public interface CursoDocenteXlsxRenderer {

    void render(CursoDocenteContent content, Path target);
}
