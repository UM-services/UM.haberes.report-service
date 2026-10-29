package um.haberes.report.reports.novedadesdocentes.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.reports.novedadesdocentes.application.service.NovedadesDocentesService;
import um.haberes.report.shared.infrastructure.web.ReportResponseBuilder;

@RestController
@RequestMapping("/api/haberes/report/novedades")
@RequiredArgsConstructor
public class NovedadesController {

    private final NovedadesDocentesService service;
    private final ReportResponseBuilder responseBuilder;

    @GetMapping("/novedadesDocentes/{facultadId}/{anho}/{mes}")
    public ResponseEntity<Resource> generateNovedadesDocentes(@PathVariable Integer facultadId,
                                                              @PathVariable Integer anho,
                                                              @PathVariable Integer mes) {
        return responseBuilder.attachment(service.generate(facultadId, anho, mes));
    }
}
