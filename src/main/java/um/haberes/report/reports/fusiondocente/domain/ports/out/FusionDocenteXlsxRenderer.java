package um.haberes.report.reports.fusiondocente.domain.ports.out;

import um.haberes.report.reports.fusiondocente.domain.model.FusionDocenteContent;

import java.nio.file.Path;

/** Exporta el mismo contenido del reporte a XLSX. */
public interface FusionDocenteXlsxRenderer {

    void render(FusionDocenteContent content, Path target);
}
