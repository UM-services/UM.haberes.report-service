package um.haberes.report.reports.cursoscontratado.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.haberes.report.reports.cursoscontratado.domain.model.CursosContratadoContent;
import um.haberes.report.reports.cursoscontratado.domain.ports.in.BuildCursosContratadoContentUseCase;
import um.haberes.report.reports.cursoscontratado.domain.ports.in.GenerateCursosContratadoReportUseCase;
import um.haberes.report.reports.cursoscontratado.domain.ports.in.GenerateCursosContratadoXlsxUseCase;
import um.haberes.report.shared.domain.model.ReportFile;

@Service
@RequiredArgsConstructor
public class CursosContratadoService {

    private final BuildCursosContratadoContentUseCase buildContent;
    private final GenerateCursosContratadoReportUseCase generatePdf;
    private final GenerateCursosContratadoXlsxUseCase generateXlsx;

    public CursosContratadoContent content(Integer anho, Integer mes) {
        return buildContent.build(anho, mes);
    }

    public ReportFile pdf(Integer anho, Integer mes) {
        return generatePdf.generate(anho, mes);
    }

    public ReportFile xlsx(Integer anho, Integer mes) {
        return generateXlsx.generate(anho, mes);
    }
}
