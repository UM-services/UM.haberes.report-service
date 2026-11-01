package um.haberes.report.reports.liquidables.domain.ports.out;

import um.haberes.report.reports.liquidables.domain.model.LiquidablesContent;

import java.nio.file.Path;

public interface LiquidablesPdfRenderer {

    void render(LiquidablesContent content, Path target);
}
