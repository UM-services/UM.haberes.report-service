package um.haberes.report.reports.comparacionremuneraciones.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.reports.comparacionremuneraciones.application.service.ComparacionRemuneracionesService;
import um.haberes.report.shared.infrastructure.web.ReportResponseBuilder;

@RestController
@RequestMapping("/api/haberes/report/comparacion")
@RequiredArgsConstructor
public class ComparacionRemuneracionesController {

    private final ComparacionRemuneracionesService service;
    private final ReportResponseBuilder responseBuilder;

    @GetMapping("/comparacionRemuneraciones/{anho}/{mes}/{anho_anterior}/{mes_anterior}")
    public ResponseEntity<Resource> generateComparacionRemuneraciones(
            @PathVariable Integer anho, @PathVariable Integer mes,
            @PathVariable Integer anho_anterior, @PathVariable Integer mes_anterior) {
        return responseBuilder.attachment(service.generate(anho, mes, anho_anterior, mes_anterior));
    }
}
