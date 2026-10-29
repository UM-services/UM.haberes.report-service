package um.haberes.report.reports.sheet.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.haberes.report.reports.sheet.domain.model.SheetCursosContent;
import um.haberes.report.reports.sheet.domain.ports.in.BuildSheetCursosContentUseCase;
import um.haberes.report.reports.sheet.domain.ports.in.GenerateSheetCursosPdfUseCase;
import um.haberes.report.reports.sheet.domain.ports.in.GenerateSheetCursosUseCase;
import um.haberes.report.shared.domain.model.ReportFile;

/**
 * Servicio del slice: un contenido para el XLSX legacy ({@code /sheet/generatecursos}) y para
 * el PDF nuevo del namespace del frontend.
 */
@Service
@RequiredArgsConstructor
public class SheetService {

    private final BuildSheetCursosContentUseCase buildContent;
    private final GenerateSheetCursosUseCase generateXlsx;
    private final GenerateSheetCursosPdfUseCase generatePdf;

    public SheetCursosContent content(Integer anho, Integer mes) {
        return buildContent.build(anho, mes);
    }

    public ReportFile generateCursos(Integer anho, Integer mes) {
        return generateXlsx.generate(anho, mes);
    }

    public ReportFile pdf(Integer anho, Integer mes) {
        return generatePdf.generate(anho, mes);
    }

    public ReportFile xlsx(Integer anho, Integer mes) {
        return generateXlsx.generate(anho, mes);
    }
}
