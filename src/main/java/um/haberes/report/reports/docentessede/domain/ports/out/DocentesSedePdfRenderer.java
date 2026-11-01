package um.haberes.report.reports.docentessede.domain.ports.out;

import um.haberes.report.reports.docentessede.domain.model.DocentesSedeContent;

import java.nio.file.Path;

public interface DocentesSedePdfRenderer {

    void render(DocentesSedeContent content, Path target);
}
