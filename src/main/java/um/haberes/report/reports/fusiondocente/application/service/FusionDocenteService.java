package um.haberes.report.reports.fusiondocente.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.haberes.report.reports.fusiondocente.domain.model.FusionDocenteContent;
import um.haberes.report.reports.fusiondocente.domain.ports.in.BuildFusionDocenteContentUseCase;
import um.haberes.report.reports.fusiondocente.domain.ports.in.GenerateFusionDocenteReportUseCase;
import um.haberes.report.reports.fusiondocente.domain.ports.in.GenerateFusionDocenteXlsxUseCase;
import um.haberes.report.shared.domain.model.ReportFile;

@Service
@RequiredArgsConstructor
public class FusionDocenteService {

    private final BuildFusionDocenteContentUseCase buildContent;
    private final GenerateFusionDocenteReportUseCase generatePdf;
    private final GenerateFusionDocenteXlsxUseCase generateXlsx;

    public FusionDocenteContent content(Integer anho, Integer mes) {
        return buildContent.build(anho, mes);
    }

    public ReportFile pdf(Integer anho, Integer mes) {
        return generatePdf.generate(anho, mes);
    }

    public ReportFile xlsx(Integer anho, Integer mes) {
        return generateXlsx.generate(anho, mes);
    }
}
