package um.haberes.report.reports.designacionesdocente.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.designacionesdocente.domain.model.DesignacionesDocenteContent;
import um.haberes.report.reports.designacionesdocente.domain.ports.in.BuildDesignacionesDocenteContentUseCase;
import um.haberes.report.reports.designacionesdocente.domain.ports.in.GenerateDesignacionesDocenteReportUseCase;
import um.haberes.report.reports.designacionesdocente.domain.ports.out.DesignacionesDocentePdfRenderer;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class GenerateDesignacionesDocenteReportUseCaseImpl
        implements GenerateDesignacionesDocenteReportUseCase {

    static final String DISPLAY_NAME = "designaciones-docente.pdf";

    private final BuildDesignacionesDocenteContentUseCase buildContent;
    private final DesignacionesDocentePdfRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer anho, Integer mes) {
        DesignacionesDocenteContent content = buildContent.build(anho, mes);
        Path target = storage.resolve(fileName(anho, mes));
        renderer.render(content, target);
        return storage.load(target, DISPLAY_NAME, ReportFile.Format.PDF);
    }

    private String fileName(Integer anho, Integer mes) {
        return "designaciones-docente." + anho + "." + mes + ".pdf";
    }
}
