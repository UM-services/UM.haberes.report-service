package um.haberes.report.reports.totalesgenerales.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.haberes.report.reports.totalesgenerales.domain.model.TotalesGeneralesContent;
import um.haberes.report.reports.totalesgenerales.domain.ports.in.BuildTotalesGeneralesContentUseCase;
import um.haberes.report.reports.totalesgenerales.domain.ports.in.GenerateTotalesGeneralesReportUseCase;
import um.haberes.report.reports.totalesgenerales.domain.ports.in.GenerateTotalesGeneralesXlsxUseCase;
import um.haberes.report.shared.domain.model.ReportFile;

@Service
@RequiredArgsConstructor
public class TotalesGeneralesService {

    private final BuildTotalesGeneralesContentUseCase buildContent;
    private final GenerateTotalesGeneralesReportUseCase generatePdf;
    private final GenerateTotalesGeneralesXlsxUseCase generateXlsx;

    /** Contenido que alimenta la tabla del frontend. */
    public TotalesGeneralesContent content(Integer anho, Integer mes) {
        return buildContent.build(anho, mes);
    }

    public ReportFile pdf(Integer anho, Integer mes) {
        return generatePdf.generate(anho, mes);
    }

    public ReportFile xlsx(Integer anho, Integer mes) {
        return generateXlsx.generate(anho, mes);
    }
}
