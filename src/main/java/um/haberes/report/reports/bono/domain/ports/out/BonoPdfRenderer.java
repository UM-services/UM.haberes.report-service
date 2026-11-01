package um.haberes.report.reports.bono.domain.ports.out;

import um.haberes.report.reports.bono.domain.model.BonoContent;

import java.nio.file.Path;

public interface BonoPdfRenderer {

    void render(BonoContent content, Path target);
}
