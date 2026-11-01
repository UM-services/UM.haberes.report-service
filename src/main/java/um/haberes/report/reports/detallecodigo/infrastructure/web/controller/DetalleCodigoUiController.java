package um.haberes.report.reports.detallecodigo.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.reports.detallecodigo.application.service.DetalleCodigoService;
import um.haberes.report.reports.detallecodigo.infrastructure.web.dto.DetalleCodigoResponse;
import um.haberes.report.reports.detallecodigo.infrastructure.web.dto.DetalleCodigoResponseMapper;
import um.haberes.report.shared.infrastructure.web.ReportResponseBuilder;

/** Namespace del frontend: tabla, PDF y XLSX del mismo contenido. */
@RestController
@RequestMapping("/api/haberes/report/ui/detalleCodigo")
@RequiredArgsConstructor
public class DetalleCodigoUiController {

    private final DetalleCodigoService service;
    private final DetalleCodigoResponseMapper mapper;
    private final ReportResponseBuilder responseBuilder;

    @GetMapping("/data/{codigoId}/{anho}/{mes}")
    public DetalleCodigoResponse data(@PathVariable Integer codigoId, @PathVariable Integer anho,
                                      @PathVariable Integer mes) {
        return mapper.toResponse(service.content(codigoId, anho, mes));
    }

    @GetMapping("/pdf/{codigoId}/{anho}/{mes}")
    public ResponseEntity<Resource> pdf(@PathVariable Integer codigoId, @PathVariable Integer anho,
                                        @PathVariable Integer mes) {
        return responseBuilder.attachment(service.pdf(codigoId, anho, mes));
    }

    @GetMapping("/xlsx/{codigoId}/{anho}/{mes}")
    public ResponseEntity<Resource> xlsx(@PathVariable Integer codigoId, @PathVariable Integer anho,
                                         @PathVariable Integer mes) {
        return responseBuilder.attachment(service.xlsx(codigoId, anho, mes));
    }
}
