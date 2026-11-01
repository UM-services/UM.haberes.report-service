package um.haberes.report.reports.totalescodigo.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.haberes.report.reports.totalescodigo.domain.model.TotalesCodigoContent;
import um.haberes.report.reports.totalescodigo.domain.ports.in.BuildTotalesCodigoContentUseCase;
import um.haberes.report.reports.totalescodigo.domain.ports.in.GenerateTotalesCodigoReportUseCase;
import um.haberes.report.reports.totalescodigo.domain.ports.in.GenerateTotalesCodigoXlsxUseCase;
import um.haberes.report.shared.domain.model.ReportFile;

@Service
@RequiredArgsConstructor
public class TotalesCodigoService {

    private final BuildTotalesCodigoContentUseCase buildContent;
    private final GenerateTotalesCodigoReportUseCase generatePdf;
    private final GenerateTotalesCodigoXlsxUseCase generateXlsx;

    public TotalesCodigoContent content(Integer anho, Integer mes) {
        return buildContent.build(anho, mes);
    }

    public ReportFile pdf(Integer anho, Integer mes) {
        return generatePdf.generate(anho, mes);
    }

    public ReportFile xlsx(Integer anho, Integer mes) {
        return generateXlsx.generate(anho, mes);
    }
}
