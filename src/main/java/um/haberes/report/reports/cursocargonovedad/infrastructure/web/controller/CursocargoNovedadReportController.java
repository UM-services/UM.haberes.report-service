package um.haberes.report.reports.cursocargonovedad.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.reports.cursocargonovedad.application.service.CursocargoNovedadReportService;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.infrastructure.web.ReportResponseBuilder;

@RestController
@RequestMapping("/api/haberes/report/cursocargonovedad")
@RequiredArgsConstructor
public class CursocargoNovedadReportController {

    private final CursocargoNovedadReportService service;
    private final ReportResponseBuilder responseBuilder;

    @GetMapping("/periodo/{anho}/{mes}")
    public ResponseEntity<Resource> generatePeriodo(@PathVariable Integer anho,
                                                    @PathVariable Integer mes) {
        ReportFile file = service.generate(anho, mes, false);
        return responseBuilder.inlinePdf(file);
    }

    @GetMapping("/pendientes/{anho}/{mes}")
    public ResponseEntity<Resource> generatePendientes(@PathVariable Integer anho,
                                                       @PathVariable Integer mes) {
        ReportFile file = service.generate(anho, mes, true);
        return responseBuilder.inlinePdf(file);
    }
}
