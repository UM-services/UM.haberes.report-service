package um.haberes.report.reports.librosueldos.domain.ports.out;

import um.haberes.report.reports.librosueldos.domain.model.LibroSueldosContent;

import java.nio.file.Path;

public interface LibroSueldosXlsxRenderer {

    void render(LibroSueldosContent content, Path target);
}
