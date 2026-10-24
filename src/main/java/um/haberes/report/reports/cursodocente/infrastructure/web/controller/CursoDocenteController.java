package um.haberes.report.reports.cursodocente.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.reports.cursodocente.application.service.CursoDocenteService;
import um.haberes.report.shared.infrastructure.web.ReportResponseBuilder;

@RestController
@RequestMapping("/api/haberes/report/curso-docente")
@RequiredArgsConstructor
public class CursoDocenteController {

    private final CursoDocenteService service;
    private final ReportResponseBuilder responseBuilder;

    @GetMapping("/pdf/{anho}/{mes}")
    public ResponseEntity<Resource> generateCursoDocente(@PathVariable Integer anho,
                                                         @PathVariable Integer mes) {
        return responseBuilder.attachment(service.generate(anho, mes));
    }
}
