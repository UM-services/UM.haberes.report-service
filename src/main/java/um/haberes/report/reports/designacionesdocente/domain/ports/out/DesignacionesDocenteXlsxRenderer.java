package um.haberes.report.reports.designacionesdocente.domain.ports.out;

import um.haberes.report.reports.designacionesdocente.domain.model.DesignacionesDocenteContent;

import java.nio.file.Path;

/** Exporta el mismo contenido del reporte a XLSX. */
public interface DesignacionesDocenteXlsxRenderer {

    void render(DesignacionesDocenteContent content, Path target);
}
