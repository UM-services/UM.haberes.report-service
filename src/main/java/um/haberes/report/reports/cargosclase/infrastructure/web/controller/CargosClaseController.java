package um.haberes.report.reports.cargosclase.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.reports.cargosclase.application.service.CargosClaseService;
import um.haberes.report.shared.infrastructure.web.ReportResponseBuilder;

@RestController
@RequestMapping("/api/haberes/report/cargos")
@RequiredArgsConstructor
public class CargosClaseController {

    private final CargosClaseService service;
    private final ReportResponseBuilder responseBuilder;

    @GetMapping("/cargos-clase")
    public ResponseEntity<Resource> generateCargosClase() {
        return responseBuilder.attachment(service.generate());
    }
}
