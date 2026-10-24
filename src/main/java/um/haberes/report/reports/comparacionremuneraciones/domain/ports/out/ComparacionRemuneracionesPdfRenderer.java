package um.haberes.report.reports.comparacionremuneraciones.domain.ports.out;

import um.haberes.report.reports.comparacionremuneraciones.domain.model.ComparacionRemuneracionesContent;

import java.nio.file.Path;

public interface ComparacionRemuneracionesPdfRenderer {

    void render(ComparacionRemuneracionesContent content, Path target);
}
