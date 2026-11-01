package um.haberes.report.reports.sheet.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.reports.sheet.application.service.SheetService;
import um.haberes.report.shared.infrastructure.web.ReportResponseBuilder;

@RestController
@RequestMapping("/api/haberes/report/sheet")
@RequiredArgsConstructor
public class SheetController {

    private final SheetService service;
    private final ReportResponseBuilder responseBuilder;

    @GetMapping("/generatecursos/{anho}/{mes}")
    public ResponseEntity<Resource> generatecursos(@PathVariable Integer anho,
                                                   @PathVariable Integer mes) {
        return responseBuilder.attachment(service.generateCursos(anho, mes));
    }
}
