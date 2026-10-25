package um.haberes.report.reports.docentessede.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.reports.docentessede.application.service.DocentesSedeService;
import um.haberes.report.shared.infrastructure.web.ReportResponseBuilder;

@RestController
@RequestMapping("/api/haberes/report/docentes")
@RequiredArgsConstructor
public class DocentesController {

    private final DocentesSedeService service;
    private final ReportResponseBuilder responseBuilder;

    @GetMapping("/docentesSede/{facultadId}/{geograficaId}/{anho}/{mes}")
    public ResponseEntity<Resource> generateDocentesSede(@PathVariable Integer facultadId,
                                                         @PathVariable Integer geograficaId,
                                                         @PathVariable Integer anho,
                                                         @PathVariable Integer mes) {
        return responseBuilder.attachment(
                service.pdf(facultadId, geograficaId, anho, mes));
    }
}
