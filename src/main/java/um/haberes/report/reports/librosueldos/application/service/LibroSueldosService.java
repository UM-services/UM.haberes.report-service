package um.haberes.report.reports.librosueldos.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.haberes.report.reports.librosueldos.domain.model.LibroSueldosContent;
import um.haberes.report.reports.librosueldos.domain.ports.in.BuildLibroSueldosContentUseCase;
import um.haberes.report.reports.librosueldos.domain.ports.in.GenerateLibroSueldosLsdUseCase;
import um.haberes.report.reports.librosueldos.domain.ports.in.GenerateLibroSueldosReportUseCase;
import um.haberes.report.reports.librosueldos.domain.ports.in.GenerateLibroSueldosXlsxUseCase;
import um.haberes.report.shared.domain.model.ReportFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LibroSueldosService {

    private final BuildLibroSueldosContentUseCase buildContent;
    private final GenerateLibroSueldosReportUseCase generatePdf;
    private final GenerateLibroSueldosXlsxUseCase generateXlsx;
    private final GenerateLibroSueldosLsdUseCase generateLsd;

    public LibroSueldosContent content(Integer anho, Integer mes) {
        return buildContent.build(anho, mes);
    }

    public ReportFile pdf(Integer anho, Integer mes) {
        return generatePdf.generate(anho, mes);
    }

    public ReportFile xlsx(Integer anho, Integer mes) {
        return generateXlsx.generate(anho, mes);
    }

    public ReportFile lsd(Integer anho, Integer mes, List<Long> legajoIds) {
        return generateLsd.generate(anho, mes, legajoIds);
    }
}
