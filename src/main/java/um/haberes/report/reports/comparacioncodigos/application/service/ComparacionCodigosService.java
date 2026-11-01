package um.haberes.report.reports.comparacioncodigos.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.haberes.report.reports.comparacioncodigos.domain.model.ComparacionCodigosContent;
import um.haberes.report.reports.comparacioncodigos.domain.ports.in.BuildComparacionCodigosContentUseCase;
import um.haberes.report.reports.comparacioncodigos.domain.ports.in.GenerateComparacionCodigosReportUseCase;
import um.haberes.report.reports.comparacioncodigos.domain.ports.in.GenerateComparacionCodigosXlsxUseCase;
import um.haberes.report.shared.domain.model.ReportFile;

@Service
@RequiredArgsConstructor
public class ComparacionCodigosService {

    private final BuildComparacionCodigosContentUseCase buildContent;
    private final GenerateComparacionCodigosReportUseCase generatePdf;
    private final GenerateComparacionCodigosXlsxUseCase generateXlsx;

    public ComparacionCodigosContent content(Integer anho, Integer mes) {
        return buildContent.build(anho, mes);
    }

    public ReportFile pdf(Integer anho, Integer mes) {
        return generatePdf.generate(anho, mes);
    }

    public ReportFile xlsx(Integer anho, Integer mes) {
        return generateXlsx.generate(anho, mes);
    }
}
