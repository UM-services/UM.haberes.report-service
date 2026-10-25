package um.haberes.report.reports.totalescodigo.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.reports.totalescodigo.application.service.TotalesCodigoService;
import um.haberes.report.shared.infrastructure.web.ReportResponseBuilder;

@RestController
@RequestMapping("/api/haberes/report/totalesCodigo")
@RequiredArgsConstructor
public class TotalesCodigoController {

    private final TotalesCodigoService service;
    private final ReportResponseBuilder responseBuilder;

    @GetMapping("/totalesCodigo/{anho}/{mes}")
    public ResponseEntity<Resource> generateTotalesCodigo(@PathVariable Integer anho, @PathVariable Integer mes) {
        return responseBuilder.attachment(service.pdf(anho, mes));
    }
}
