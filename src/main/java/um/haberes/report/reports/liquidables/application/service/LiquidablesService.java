package um.haberes.report.reports.liquidables.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.haberes.report.reports.liquidables.domain.model.LiquidablesContent;
import um.haberes.report.reports.liquidables.domain.ports.in.BuildLiquidablesContentUseCase;
import um.haberes.report.reports.liquidables.domain.ports.in.GenerateLiquidablesReportUseCase;
import um.haberes.report.reports.liquidables.domain.ports.in.GenerateLiquidablesXlsxUseCase;
import um.haberes.report.shared.domain.model.ReportFile;

/** Punto de entrada del slice: un contenido para la tabla, el PDF y el XLSX. */
@Service
@RequiredArgsConstructor
public class LiquidablesService {

    private final BuildLiquidablesContentUseCase buildContent;
    private final GenerateLiquidablesReportUseCase generatePdf;
    private final GenerateLiquidablesXlsxUseCase generateXlsx;

    public LiquidablesContent content() {
        return buildContent.build();
    }

    public ReportFile pdf() {
        return generatePdf.generate();
    }

    public ReportFile xlsx() {
        return generateXlsx.generate();
    }
}
