package um.haberes.report.reports.cursodocente.domain.ports.out;

import um.haberes.report.reports.cursodocente.domain.model.CursoDocenteContent;

import java.nio.file.Path;

public interface CursoDocentePdfRenderer {

    void render(CursoDocenteContent content, Path target);
}
