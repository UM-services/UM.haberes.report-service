package um.haberes.report.reports.comparacioncodigos.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.reports.comparacioncodigos.application.service.ComparacionCodigosService;
import um.haberes.report.shared.infrastructure.web.ReportResponseBuilder;

@RestController
@RequestMapping("/api/haberes/report/comparacionCodigos")
@RequiredArgsConstructor
public class ComparacionCodigosController {

    private final ComparacionCodigosService service;
    private final ReportResponseBuilder responseBuilder;

    @GetMapping("/comparacionCodigos/{anho}/{mes}")
    public ResponseEntity<Resource> generateComparacionCodigos(@PathVariable Integer anho,
                                                               @PathVariable Integer mes) {
        return responseBuilder.attachment(service.pdf(anho, mes));
    }
}
