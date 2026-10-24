package um.haberes.report.reports.designacionesdetalle.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.reports.designacionesdetalle.application.service.DesignacionesDetalleService;
import um.haberes.report.shared.infrastructure.web.ReportResponseBuilder;

@RestController
@RequestMapping("/api/haberes/report/designacionesDetalle")
@RequiredArgsConstructor
public class DesignacionesDetalleController {

    private final DesignacionesDetalleService service;
    private final ReportResponseBuilder responseBuilder;

    @GetMapping("/{legajoId}/{anho}/{mes}")
    public ResponseEntity<Resource> generate(@PathVariable Long legajoId,
                                             @PathVariable Integer anho,
                                             @PathVariable Integer mes) {
        return responseBuilder.inlinePdf(service.generate(legajoId, anho, mes));
    }
}
