package um.haberes.report.reports.anotador.domain.ports.out;

import um.haberes.report.reports.anotador.domain.model.AnotadorContent;

import java.nio.file.Path;

public interface AnotadorPdfRenderer {

    void render(AnotadorContent content, Path target);
}
