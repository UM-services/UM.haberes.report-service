package um.haberes.report.reports.sheet.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.haberes.report.reports.sheet.domain.ports.in.GenerateSheetCursosUseCase;
import um.haberes.report.shared.domain.model.ReportFile;

@Service
@RequiredArgsConstructor
public class SheetService {

    private final GenerateSheetCursosUseCase generateReport;

    public ReportFile generateCursos(Integer anho, Integer mes) {
        return generateReport.generate(anho, mes);
    }
}
