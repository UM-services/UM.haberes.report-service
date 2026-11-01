package um.haberes.report.reports.totalesgenerales.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.reports.totalesgenerales.application.service.TotalesGeneralesService;
import um.haberes.report.shared.infrastructure.web.ReportResponseBuilder;

@RestController
@RequestMapping("/api/haberes/report/totalesGenerales")
@RequiredArgsConstructor
public class TotalesGeneralesController {

    private final TotalesGeneralesService service;
    private final ReportResponseBuilder responseBuilder;

    @GetMapping("/totalesGenerales/{anho}/{mes}")
    public ResponseEntity<Resource> generateTotalesGenerales(@PathVariable Integer anho,
                                                             @PathVariable Integer mes) {
        return responseBuilder.attachment(service.pdf(anho, mes));
    }
}
