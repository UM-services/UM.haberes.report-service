package um.haberes.report.reports.fusiondocente.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.reports.fusiondocente.application.service.FusionDocenteService;
import um.haberes.report.shared.infrastructure.web.ReportResponseBuilder;

@RestController
@RequestMapping("/api/haberes/report/fusionDocente")
@RequiredArgsConstructor
public class FusionDocenteController {

    private final FusionDocenteService service;
    private final ReportResponseBuilder responseBuilder;

    @GetMapping("/fusionDocente/{anho}/{mes}")
    public ResponseEntity<Resource> generateFusionDocente(@PathVariable Integer anho,
                                                          @PathVariable Integer mes) {
        return responseBuilder.attachment(service.pdf(anho, mes));
    }
}
