package um.haberes.report.reports.bono.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.reports.bono.application.service.BonoService;
import um.haberes.report.reports.bono.infrastructure.web.dto.BonosDependenciaResponse;
import um.haberes.report.reports.bono.infrastructure.web.dto.BonosDependenciaResponseMapper;
import um.haberes.report.shared.infrastructure.web.ReportResponseBuilder;

/**
 * Namespace del frontend para el bono por dependencia: grilla, PDF consolidado y envío unitario.
 *
 * <p>La preparación de la actividad y la auditoría de la impresión se resuelven en el core
 * ({@code POST /bono/dependencia/.../print-prepare}), que es quien conoce la IP del cliente;
 * estas rutas no reciben ni registran IP.
 */
@RestController
@RequestMapping("/api/haberes/report/ui/bonoDependencia")
@RequiredArgsConstructor
public class BonosDependenciaUiController {

    private final BonoService service;
    private final BonosDependenciaResponseMapper mapper;
    private final ReportResponseBuilder responseBuilder;

    @GetMapping("/data/{anho}/{mes}/{dependenciaId}/{salida}")
    public BonosDependenciaResponse data(@PathVariable Integer anho, @PathVariable Integer mes,
                                         @PathVariable Integer dependenciaId,
                                         @PathVariable String salida) {
        return mapper.toResponse(service.contenidoDependencia(anho, mes, dependenciaId, salida));
    }

    @GetMapping("/pdf/{anho}/{mes}/{dependenciaId}/{salida}")
    public ResponseEntity<Resource> pdf(@PathVariable Integer anho, @PathVariable Integer mes,
                                        @PathVariable Integer dependenciaId,
                                        @PathVariable String salida) {
        return responseBuilder.attachment(
                service.generatePdfDependenciaUi(anho, mes, dependenciaId, salida));
    }

    @GetMapping("/sendBono/{legajoId}/{anho}/{mes}")
    public String sendBono(@PathVariable Long legajoId, @PathVariable Integer anho,
                           @PathVariable Integer mes) {
        return service.sendBonoUi(legajoId, anho, mes);
    }
}
