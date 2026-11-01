package um.haberes.report.reports.basicosperiodo.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.haberes.report.reports.basicosperiodo.domain.model.BasicosPeriodoContent;
import um.haberes.report.reports.basicosperiodo.domain.ports.in.BuildBasicosPeriodoContentUseCase;
import um.haberes.report.reports.basicosperiodo.domain.ports.in.GenerateBasicosPeriodoReportUseCase;
import um.haberes.report.reports.basicosperiodo.domain.ports.in.GenerateBasicosPeriodoXlsxUseCase;
import um.haberes.report.shared.domain.model.ReportFile;

@Service
@RequiredArgsConstructor
public class BasicosPeriodoService {

    private final BuildBasicosPeriodoContentUseCase buildContent;
    private final GenerateBasicosPeriodoReportUseCase generatePdf;
    private final GenerateBasicosPeriodoXlsxUseCase generateXlsx;

    public BasicosPeriodoContent content(Integer anho, Integer mes) {
        return buildContent.build(anho, mes);
    }

    public ReportFile pdf(Integer anho, Integer mes) {
        return generatePdf.generate(anho, mes);
    }

    public ReportFile xlsx(Integer anho, Integer mes) {
        return generateXlsx.generate(anho, mes);
    }
}
