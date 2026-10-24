package um.haberes.report.reports.designacionesdocente.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.reports.designacionesdocente.application.service.DesignacionesDocenteService;
import um.haberes.report.shared.infrastructure.web.ReportResponseBuilder;

@RestController
@RequestMapping("/api/haberes/report/designacionesDocente")
@RequiredArgsConstructor
public class DesignacionesDocenteController {

    private final DesignacionesDocenteService service;
    private final ReportResponseBuilder responseBuilder;

    @GetMapping("/pdf/{anho}/{mes}")
    public ResponseEntity<Resource> generateDesignacionesDocente(@PathVariable Integer anho,
                                                                 @PathVariable Integer mes) {
        return responseBuilder.attachment(service.generate(anho, mes));
    }
}
