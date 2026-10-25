package um.haberes.report.reports.cantidadcargos.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.haberes.report.reports.cantidadcargos.domain.model.CantidadCargosContent;
import um.haberes.report.reports.cantidadcargos.domain.ports.in.BuildCantidadCargosContentUseCase;
import um.haberes.report.reports.cantidadcargos.domain.ports.in.GenerateCantidadCargosReportUseCase;
import um.haberes.report.reports.cantidadcargos.domain.ports.in.GenerateCantidadCargosXlsxUseCase;
import um.haberes.report.shared.domain.model.ReportFile;

@Service
@RequiredArgsConstructor
public class CantidadCargosService {

    private final BuildCantidadCargosContentUseCase buildContent;
    private final GenerateCantidadCargosReportUseCase generatePdf;
    private final GenerateCantidadCargosXlsxUseCase generateXlsx;

    public CantidadCargosContent content(Integer anho, Integer mes) {
        return buildContent.build(anho, mes);
    }

    public ReportFile pdf(Integer anho, Integer mes) {
        return generatePdf.generate(anho, mes);
    }

    public ReportFile xlsx(Integer anho, Integer mes) {
        return generateXlsx.generate(anho, mes);
    }
}
