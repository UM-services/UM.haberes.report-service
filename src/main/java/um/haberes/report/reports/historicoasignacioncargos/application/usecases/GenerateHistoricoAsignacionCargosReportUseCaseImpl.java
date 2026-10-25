package um.haberes.report.reports.historicoasignacioncargos.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.historicoasignacioncargos.domain.model.HistoricoAsignacionCargosContent;
import um.haberes.report.reports.historicoasignacioncargos.domain.ports.in.BuildHistoricoAsignacionCargosContentUseCase;
import um.haberes.report.reports.historicoasignacioncargos.domain.ports.in.GenerateHistoricoAsignacionCargosReportUseCase;
import um.haberes.report.reports.historicoasignacioncargos.domain.ports.out.HistoricoAsignacionCargosPdfRenderer;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;

/**
 * Conserva el comportamiento del reporte previo: emite sólo el encabezado con la categoría.
 * El cuerpo requiere una spec de producto (fuera de alcance).
 */
@Component
@RequiredArgsConstructor
public class GenerateHistoricoAsignacionCargosReportUseCaseImpl
        implements GenerateHistoricoAsignacionCargosReportUseCase {

    static final String DISPLAY_NAME = "historicoAsignacionDeCargos.pdf";

    private final BuildHistoricoAsignacionCargosContentUseCase buildContent;
    private final HistoricoAsignacionCargosPdfRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer categoriaId) {
        HistoricoAsignacionCargosContent content = buildContent.build(categoriaId);
        Path target = storage.resolve(fileName(categoriaId));
        renderer.render(content, target);
        return storage.load(target, DISPLAY_NAME, ReportFile.Format.PDF);
    }

    private String fileName(Integer categoriaId) {
        return "historicoAsignacionDeCargos." + categoriaId + ".pdf";
    }
}
