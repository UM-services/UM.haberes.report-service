package um.haberes.report.reports.detallecodigo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.detallecodigo.domain.model.DetalleCodigoContent;
import um.haberes.report.reports.detallecodigo.domain.ports.in.BuildDetalleCodigoContentUseCase;
import um.haberes.report.reports.detallecodigo.domain.ports.in.GenerateDetalleCodigoReportUseCase;
import um.haberes.report.reports.detallecodigo.domain.ports.out.DetalleCodigoPdfRenderer;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class GenerateDetalleCodigoReportUseCaseImpl implements GenerateDetalleCodigoReportUseCase {

    static final String DISPLAY_NAME = "detallecodigo.pdf";

    private final BuildDetalleCodigoContentUseCase buildContent;
    private final DetalleCodigoPdfRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer codigoId, Integer anho, Integer mes) {
        DetalleCodigoContent content = buildContent.build(codigoId, anho, mes);
        Path target = storage.resolve(fileName(codigoId, anho, mes));
        renderer.render(content, target);
        return storage.load(target, DISPLAY_NAME, ReportFile.Format.PDF);
    }

    private String fileName(Integer codigoId, Integer anho, Integer mes) {
        return "detallecodigo." + codigoId + "." + anho + "." + mes + ".pdf";
    }
}
