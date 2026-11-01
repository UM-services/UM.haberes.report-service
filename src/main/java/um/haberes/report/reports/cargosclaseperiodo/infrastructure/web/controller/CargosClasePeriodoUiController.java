package um.haberes.report.reports.cargosclaseperiodo.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.reports.cargosclaseperiodo.application.service.CargosClasePeriodoService;
import um.haberes.report.reports.cargosclaseperiodo.infrastructure.web.dto.CargosClasePeriodoResponse;
import um.haberes.report.reports.cargosclaseperiodo.infrastructure.web.dto.CargosClasePeriodoResponseMapper;
import um.haberes.report.shared.infrastructure.web.ReportResponseBuilder;

/** Namespace del frontend: tabla, PDF y XLSX del mismo contenido. */
@RestController
@RequestMapping("/api/haberes/report/ui/cargosClasePeriodo")
@RequiredArgsConstructor
public class CargosClasePeriodoUiController {

    private final CargosClasePeriodoService service;
    private final CargosClasePeriodoResponseMapper mapper;
    private final ReportResponseBuilder responseBuilder;

    @GetMapping("/data/{facultadId}/{anho}/{mes}")
    public CargosClasePeriodoResponse data(@PathVariable Integer facultadId,
                                          @PathVariable Integer anho, @PathVariable Integer mes) {
        return mapper.toResponse(service.content(facultadId, anho, mes));
    }

    @GetMapping("/pdf/{facultadId}/{anho}/{mes}")
    public ResponseEntity<Resource> pdf(@PathVariable Integer facultadId,
                                        @PathVariable Integer anho, @PathVariable Integer mes) {
        return responseBuilder.attachment(service.pdf(facultadId, anho, mes));
    }

    @GetMapping("/xlsx/{facultadId}/{anho}/{mes}")
    public ResponseEntity<Resource> xlsx(@PathVariable Integer facultadId,
                                         @PathVariable Integer anho, @PathVariable Integer mes) {
        return responseBuilder.attachment(service.xlsx(facultadId, anho, mes));
    }
}
