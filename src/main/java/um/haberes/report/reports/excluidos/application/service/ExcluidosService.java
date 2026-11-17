package um.haberes.report.reports.excluidos.application.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import um.haberes.report.reports.excluidos.domain.model.ExcluidosContent;
import um.haberes.report.reports.excluidos.domain.ports.in.BuildExcluidosContentUseCase;
import um.haberes.report.reports.excluidos.domain.ports.in.GenerateExcluidosPdfUseCase;
import um.haberes.report.reports.excluidos.domain.ports.in.GenerateExcluidosXlsxUseCase;
import um.haberes.report.shared.domain.model.ReportFile;

@Service
@RequiredArgsConstructor
public class ExcluidosService {

    private final BuildExcluidosContentUseCase buildContent;
    private final GenerateExcluidosPdfUseCase generatePdf;
    private final GenerateExcluidosXlsxUseCase generateXlsx;

    public ExcluidosContent content(Integer anho, Integer mes) {
        return buildContent.build(anho, mes);
    }

    public ReportFile pdf(Integer anho, Integer mes) {
        return generatePdf.generate(anho, mes);
    }

    public ReportFile xlsx(Integer anho, Integer mes) {
        return generateXlsx.generate(anho, mes);
    }
}
