package um.haberes.report.reports.docentessede.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.reports.docentessede.application.service.DocentesSedeService;
import um.haberes.report.reports.docentessede.infrastructure.web.dto.DocentesSedeResponse;
import um.haberes.report.reports.docentessede.infrastructure.web.dto.DocentesSedeResponseMapper;
import um.haberes.report.shared.infrastructure.web.ReportResponseBuilder;

/** Namespace del frontend: tabla, PDF y XLSX del mismo contenido. */
@RestController
@RequestMapping("/api/haberes/report/ui/docentesSede")
@RequiredArgsConstructor
public class DocentesSedeUiController {

    private final DocentesSedeService service;
    private final DocentesSedeResponseMapper mapper;
    private final ReportResponseBuilder responseBuilder;

    @GetMapping("/data/{facultadId}/{geograficaId}/{anho}/{mes}")
    public DocentesSedeResponse data(@PathVariable Integer facultadId,
                                    @PathVariable Integer geograficaId,
                                    @PathVariable Integer anho, @PathVariable Integer mes) {
        return mapper.toResponse(service.content(facultadId, geograficaId, anho, mes));
    }

    @GetMapping("/pdf/{facultadId}/{geograficaId}/{anho}/{mes}")
    public ResponseEntity<Resource> pdf(@PathVariable Integer facultadId,
                                        @PathVariable Integer geograficaId,
                                        @PathVariable Integer anho, @PathVariable Integer mes) {
        return responseBuilder.attachment(service.pdf(facultadId, geograficaId, anho, mes));
    }

    @GetMapping("/xlsx/{facultadId}/{geograficaId}/{anho}/{mes}")
    public ResponseEntity<Resource> xlsx(@PathVariable Integer facultadId,
                                         @PathVariable Integer geograficaId,
                                         @PathVariable Integer anho, @PathVariable Integer mes) {
        return responseBuilder.attachment(service.xlsx(facultadId, geograficaId, anho, mes));
    }
}
