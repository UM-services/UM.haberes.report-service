package um.haberes.report.reports.docentessede.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.haberes.report.reports.docentessede.domain.model.DocentesSedeContent;
import um.haberes.report.reports.docentessede.domain.ports.in.BuildDocentesSedeContentUseCase;
import um.haberes.report.reports.docentessede.domain.ports.in.GenerateDocentesSedeReportUseCase;
import um.haberes.report.reports.docentessede.domain.ports.in.GenerateDocentesSedeXlsxUseCase;
import um.haberes.report.shared.domain.model.ReportFile;

@Service
@RequiredArgsConstructor
public class DocentesSedeService {

    private final BuildDocentesSedeContentUseCase buildContent;
    private final GenerateDocentesSedeReportUseCase generatePdf;
    private final GenerateDocentesSedeXlsxUseCase generateXlsx;

    public DocentesSedeContent content(Integer facultadId, Integer geograficaId, Integer anho,
                                       Integer mes) {
        return buildContent.build(facultadId, geograficaId, anho, mes);
    }

    public ReportFile pdf(Integer facultadId, Integer geograficaId, Integer anho, Integer mes) {
        return generatePdf.generate(facultadId, geograficaId, anho, mes);
    }

    public ReportFile xlsx(Integer facultadId, Integer geograficaId, Integer anho, Integer mes) {
        return generateXlsx.generate(facultadId, geograficaId, anho, mes);
    }
}
