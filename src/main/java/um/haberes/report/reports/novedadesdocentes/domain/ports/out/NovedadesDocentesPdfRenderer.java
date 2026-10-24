package um.haberes.report.reports.novedadesdocentes.domain.ports.out;

import um.haberes.report.reports.novedadesdocentes.domain.model.NovedadesDocentesContent;

import java.nio.file.Path;

public interface NovedadesDocentesPdfRenderer {

    void render(NovedadesDocentesContent content, Path target);
}
