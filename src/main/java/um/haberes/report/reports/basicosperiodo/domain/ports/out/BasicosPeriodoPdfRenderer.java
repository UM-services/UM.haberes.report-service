package um.haberes.report.reports.basicosperiodo.domain.ports.out;

import um.haberes.report.reports.basicosperiodo.domain.model.BasicosPeriodoContent;

import java.nio.file.Path;

public interface BasicosPeriodoPdfRenderer {

    void render(BasicosPeriodoContent content, Path target);
}
