package um.haberes.report.reports.designacionesdocente.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.designacionesdocente.domain.model.DesignacionesDocenteContent;
import um.haberes.report.reports.designacionesdocente.domain.ports.in.BuildDesignacionesDocenteContentUseCase;
import um.haberes.report.reports.designacionesdocente.domain.ports.in.GenerateDesignacionesDocenteXlsxUseCase;
import um.haberes.report.reports.designacionesdocente.domain.ports.out.DesignacionesDocenteXlsxRenderer;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class GenerateDesignacionesDocenteXlsxUseCaseImpl
        implements GenerateDesignacionesDocenteXlsxUseCase {

    static final String DISPLAY_NAME = "designaciones-docente.xlsx";

    private final BuildDesignacionesDocenteContentUseCase buildContent;
    private final DesignacionesDocenteXlsxRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer anho, Integer mes) {
        DesignacionesDocenteContent content = buildContent.build(anho, mes);
        Path target = storage.resolve("designaciones-docente." + anho + "." + mes + ".xlsx");
        renderer.render(content, target);
        return storage.load(target, DISPLAY_NAME, ReportFile.Format.XLSX);
    }
}
