package um.haberes.report.reports.historicoasignacioncargos.domain.ports.out;

import um.haberes.report.reports.historicoasignacioncargos.domain.model.HistoricoAsignacionCargosContent;

import java.nio.file.Path;

public interface HistoricoAsignacionCargosPdfRenderer {

    void render(HistoricoAsignacionCargosContent content, Path target);
}
