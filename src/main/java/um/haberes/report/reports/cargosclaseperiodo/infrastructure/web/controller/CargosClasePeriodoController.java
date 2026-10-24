package um.haberes.report.reports.cargosclaseperiodo.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.reports.cargosclaseperiodo.application.service.CargosClasePeriodoService;
import um.haberes.report.shared.infrastructure.web.ReportResponseBuilder;

@RestController
@RequestMapping("/api/haberes/report/cargosClasePeriodo")
@RequiredArgsConstructor
public class CargosClasePeriodoController {

    private final CargosClasePeriodoService service;
    private final ReportResponseBuilder responseBuilder;

    @GetMapping("/pdf/{facultadId}/{anho}/{mes}")
    public ResponseEntity<Resource> generateCargosClasePeriodo(@PathVariable Integer facultadId,
                                                               @PathVariable Integer anho,
                                                               @PathVariable Integer mes) {
        return responseBuilder.attachment(service.generate(facultadId, anho, mes));
    }
}
