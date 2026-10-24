package um.haberes.report.reports.designacionesdocente.domain.ports.out;

import um.haberes.report.reports.designacionesdocente.domain.model.DesignacionesDocenteContent;

import java.nio.file.Path;

public interface DesignacionesDocentePdfRenderer {

    void render(DesignacionesDocenteContent content, Path target);
}
