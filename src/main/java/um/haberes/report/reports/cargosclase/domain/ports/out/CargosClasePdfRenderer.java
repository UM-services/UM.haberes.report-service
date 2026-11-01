package um.haberes.report.reports.cargosclase.domain.ports.out;

import um.haberes.report.reports.cargosclase.domain.model.CargosClaseContent;

import java.nio.file.Path;

public interface CargosClasePdfRenderer {

    void render(CargosClaseContent content, Path target);
}
