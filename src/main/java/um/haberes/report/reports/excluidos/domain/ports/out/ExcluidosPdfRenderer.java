package um.haberes.report.reports.excluidos.domain.ports.out;

import java.nio.file.Path;

import um.haberes.report.reports.excluidos.domain.model.ExcluidosContent;

public interface ExcluidosPdfRenderer {

    void render(ExcluidosContent content, Path target);
}
