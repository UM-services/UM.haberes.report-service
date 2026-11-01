package um.haberes.report.reports.fusiondocente.domain.ports.out;

import um.haberes.report.reports.fusiondocente.domain.model.FusionDocenteContent;

import java.nio.file.Path;

public interface FusionDocentePdfRenderer {

    void render(FusionDocenteContent content, Path target);
}
