package um.haberes.report.reports.comparacionremuneraciones.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.reports.comparacionremuneraciones.application.service.ComparacionRemuneracionesService;
import um.haberes.report.reports.comparacionremuneraciones.infrastructure.web.dto.ComparacionRemuneracionesResponse;
import um.haberes.report.reports.comparacionremuneraciones.infrastructure.web.dto.ComparacionRemuneracionesResponseMapper;
import um.haberes.report.shared.infrastructure.web.ReportResponseBuilder;

/** Namespace del frontend: tabla, PDF y XLSX del mismo contenido, con los dos períodos. */
@RestController
@RequestMapping("/api/haberes/report/ui/comparacionRemuneraciones")
@RequiredArgsConstructor
public class ComparacionRemuneracionesUiController {

    private final ComparacionRemuneracionesService service;
    private final ComparacionRemuneracionesResponseMapper mapper;
    private final ReportResponseBuilder responseBuilder;

    @GetMapping("/data/{anho}/{mes}/{anhoAnterior}/{mesAnterior}")
    public ComparacionRemuneracionesResponse data(@PathVariable Integer anho,
                                                  @PathVariable Integer mes,
                                                  @PathVariable Integer anhoAnterior,
                                                  @PathVariable Integer mesAnterior) {
        return mapper.toResponse(service.content(anho, mes, anhoAnterior, mesAnterior));
    }

    @GetMapping("/pdf/{anho}/{mes}/{anhoAnterior}/{mesAnterior}")
    public ResponseEntity<Resource> pdf(@PathVariable Integer anho, @PathVariable Integer mes,
                                        @PathVariable Integer anhoAnterior,
                                        @PathVariable Integer mesAnterior) {
        return responseBuilder.attachment(service.pdf(anho, mes, anhoAnterior, mesAnterior));
    }

    @GetMapping("/xlsx/{anho}/{mes}/{anhoAnterior}/{mesAnterior}")
    public ResponseEntity<Resource> xlsx(@PathVariable Integer anho, @PathVariable Integer mes,
                                         @PathVariable Integer anhoAnterior,
                                         @PathVariable Integer mesAnterior) {
        return responseBuilder.attachment(service.xlsx(anho, mes, anhoAnterior, mesAnterior));
    }
}
