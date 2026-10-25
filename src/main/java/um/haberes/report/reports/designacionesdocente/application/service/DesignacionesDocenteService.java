package um.haberes.report.reports.designacionesdocente.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.haberes.report.reports.designacionesdocente.domain.model.DesignacionesDocenteContent;
import um.haberes.report.reports.designacionesdocente.domain.ports.in.BuildDesignacionesDocenteContentUseCase;
import um.haberes.report.reports.designacionesdocente.domain.ports.in.GenerateDesignacionesDocenteReportUseCase;
import um.haberes.report.reports.designacionesdocente.domain.ports.in.GenerateDesignacionesDocenteXlsxUseCase;
import um.haberes.report.shared.domain.model.ReportFile;

@Service
@RequiredArgsConstructor
public class DesignacionesDocenteService {

    private final BuildDesignacionesDocenteContentUseCase buildContent;
    private final GenerateDesignacionesDocenteReportUseCase generatePdf;
    private final GenerateDesignacionesDocenteXlsxUseCase generateXlsx;

    public DesignacionesDocenteContent content(Integer anho, Integer mes) {
        return buildContent.build(anho, mes);
    }

    public ReportFile pdf(Integer anho, Integer mes) {
        return generatePdf.generate(anho, mes);
    }

    public ReportFile xlsx(Integer anho, Integer mes) {
        return generateXlsx.generate(anho, mes);
    }
}
