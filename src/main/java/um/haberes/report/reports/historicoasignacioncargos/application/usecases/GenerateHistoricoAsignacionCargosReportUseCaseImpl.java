package um.haberes.report.reports.historicoasignacioncargos.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.historicoasignacioncargos.domain.model.HistoricoAsignacionCargosContent;
import um.haberes.report.reports.historicoasignacioncargos.domain.ports.in.GenerateHistoricoAsignacionCargosReportUseCase;
import um.haberes.report.reports.historicoasignacioncargos.domain.ports.out.HistoricoAsignacionCargosPdfRenderer;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.CategoriaCatalogPort;
import um.haberes.report.shared.domain.ports.out.DependenciaCatalogPort;
import um.haberes.report.shared.domain.ports.out.FacultadCatalogPort;
import um.haberes.report.shared.domain.ports.out.GeograficaCatalogPort;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;

/**
 * Conserva el comportamiento del reporte legacy: consulta los cuatro catalogos y emite solo
 * el encabezado. Restaurar el cuerpo requiere una spec de producto (fuera de alcance).
 */
@Component
@RequiredArgsConstructor
public class GenerateHistoricoAsignacionCargosReportUseCaseImpl
        implements GenerateHistoricoAsignacionCargosReportUseCase {

    static final String DISPLAY_NAME = "historicoAsignacionDeCargos.pdf";

    private final CategoriaCatalogPort categoriaCatalogPort;
    private final DependenciaCatalogPort dependenciaCatalogPort;
    private final FacultadCatalogPort facultadCatalogPort;
    private final GeograficaCatalogPort geograficaCatalogPort;
    private final HistoricoAsignacionCargosPdfRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer categoriaId) {
        categoriaCatalogPort.findAll();
        dependenciaCatalogPort.findAll();
        facultadCatalogPort.findAll();
        geograficaCatalogPort.findAll();

        Path target = storage.resolve(fileName(categoriaId));
        renderer.render(new HistoricoAsignacionCargosContent(categoriaId), target);
        return storage.load(target, DISPLAY_NAME, ReportFile.Format.PDF);
    }

    private String fileName(Integer categoriaId) {
        return "historicoAsignacionDeCargos." + categoriaId + ".pdf";
    }
}
