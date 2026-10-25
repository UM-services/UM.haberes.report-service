package um.haberes.report.reports.cursodocente.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.reports.cursodocente.application.service.CursoDocenteService;
import um.haberes.report.reports.cursodocente.infrastructure.web.dto.CursoDocenteResponse;
import um.haberes.report.reports.cursodocente.infrastructure.web.dto.CursoDocenteResponseMapper;
import um.haberes.report.shared.infrastructure.web.ReportResponseBuilder;

/** Namespace del frontend: tabla, PDF y XLSX del mismo contenido. */
@RestController
@RequestMapping("/api/haberes/report/ui/cursoDocente")
@RequiredArgsConstructor
public class CursoDocenteUiController {

    private final CursoDocenteService service;
    private final CursoDocenteResponseMapper mapper;
    private final ReportResponseBuilder responseBuilder;

    @GetMapping("/data/{anho}/{mes}")
    public CursoDocenteResponse data(@PathVariable Integer anho, @PathVariable Integer mes) {
        return mapper.toResponse(service.content(anho, mes));
    }

    @GetMapping("/pdf/{anho}/{mes}")
    public ResponseEntity<Resource> pdf(@PathVariable Integer anho, @PathVariable Integer mes) {
        return responseBuilder.attachment(service.pdf(anho, mes));
    }

    @GetMapping("/xlsx/{anho}/{mes}")
    public ResponseEntity<Resource> xlsx(@PathVariable Integer anho, @PathVariable Integer mes) {
        return responseBuilder.attachment(service.xlsx(anho, mes));
    }
}
