package um.haberes.report.reports.cursodocente.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.haberes.report.reports.cursodocente.domain.model.CursoDocenteContent;
import um.haberes.report.reports.cursodocente.domain.ports.in.BuildCursoDocenteContentUseCase;
import um.haberes.report.reports.cursodocente.domain.ports.in.GenerateCursoDocenteReportUseCase;
import um.haberes.report.reports.cursodocente.domain.ports.in.GenerateCursoDocenteXlsxUseCase;
import um.haberes.report.shared.domain.model.ReportFile;

@Service
@RequiredArgsConstructor
public class CursoDocenteService {

    private final BuildCursoDocenteContentUseCase buildContent;
    private final GenerateCursoDocenteReportUseCase generatePdf;
    private final GenerateCursoDocenteXlsxUseCase generateXlsx;

    public CursoDocenteContent content(Integer anho, Integer mes) {
        return buildContent.build(anho, mes);
    }

    public ReportFile pdf(Integer anho, Integer mes) {
        return generatePdf.generate(anho, mes);
    }

    public ReportFile xlsx(Integer anho, Integer mes) {
        return generateXlsx.generate(anho, mes);
    }
}
