package um.haberes.report.reports.comparacionremuneraciones.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.haberes.report.reports.comparacionremuneraciones.domain.model.ComparacionRemuneracionesContent;
import um.haberes.report.reports.comparacionremuneraciones.domain.ports.in.BuildComparacionRemuneracionesContentUseCase;
import um.haberes.report.reports.comparacionremuneraciones.domain.ports.in.GenerateComparacionRemuneracionesReportUseCase;
import um.haberes.report.reports.comparacionremuneraciones.domain.ports.in.GenerateComparacionRemuneracionesXlsxUseCase;
import um.haberes.report.shared.domain.model.ReportFile;

@Service
@RequiredArgsConstructor
public class ComparacionRemuneracionesService {

    private final BuildComparacionRemuneracionesContentUseCase buildContent;
    private final GenerateComparacionRemuneracionesReportUseCase generatePdf;
    private final GenerateComparacionRemuneracionesXlsxUseCase generateXlsx;

    public ComparacionRemuneracionesContent content(Integer anho, Integer mes, Integer anhoAnterior,
                                                    Integer mesAnterior) {
        return buildContent.build(anho, mes, anhoAnterior, mesAnterior);
    }

    public ReportFile pdf(Integer anho, Integer mes, Integer anhoAnterior, Integer mesAnterior) {
        return generatePdf.generate(anho, mes, anhoAnterior, mesAnterior);
    }

    public ReportFile xlsx(Integer anho, Integer mes, Integer anhoAnterior, Integer mesAnterior) {
        return generateXlsx.generate(anho, mes, anhoAnterior, mesAnterior);
    }
}
