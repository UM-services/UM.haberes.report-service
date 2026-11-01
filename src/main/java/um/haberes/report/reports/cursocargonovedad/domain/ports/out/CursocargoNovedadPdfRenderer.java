package um.haberes.report.reports.cursocargonovedad.domain.ports.out;

import um.haberes.report.reports.cursocargonovedad.domain.model.CursocargoNovedadContent;

import java.nio.file.Path;

public interface CursocargoNovedadPdfRenderer {

    void render(CursocargoNovedadContent content, Path target);
}
