package um.haberes.report.reports.anotador.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.reports.anotador.application.service.AnotadorService;
import um.haberes.report.shared.infrastructure.web.ReportResponseBuilder;

@RestController
@RequestMapping("/api/haberes/report/anotador")
@RequiredArgsConstructor
public class AnotadorController {

    private final AnotadorService service;
    private final ReportResponseBuilder responseBuilder;

    @GetMapping("/anotadorPendientes/{facultadId}/{anho}/{mes}")
    public ResponseEntity<Resource> pendientesFacultad(@PathVariable Integer facultadId,
                                                       @PathVariable Integer anho,
                                                       @PathVariable Integer mes) {
        return responseBuilder.attachment(service.generate(facultadId, anho, mes, true));
    }

    @GetMapping("/anotadorAutorizadosRechazados/{facultadId}/{anho}/{mes}")
    public ResponseEntity<Resource> revisadosFacultad(@PathVariable Integer facultadId,
                                                      @PathVariable Integer anho,
                                                      @PathVariable Integer mes) {
        return responseBuilder.attachment(service.generate(facultadId, anho, mes, false));
    }

    @GetMapping("/anotadorPendientes/periodo/{anho}/{mes}")
    public ResponseEntity<Resource> pendientesPeriodo(@PathVariable Integer anho,
                                                      @PathVariable Integer mes) {
        return responseBuilder.attachment(service.generate(null, anho, mes, true));
    }

    @GetMapping("/anotadorAutorizadosRechazados/periodo/{anho}/{mes}")
    public ResponseEntity<Resource> revisadosPeriodo(@PathVariable Integer anho,
                                                     @PathVariable Integer mes) {
        return responseBuilder.attachment(service.generate(null, anho, mes, false));
    }
}
