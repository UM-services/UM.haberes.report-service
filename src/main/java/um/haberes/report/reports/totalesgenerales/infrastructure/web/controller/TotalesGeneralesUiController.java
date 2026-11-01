package um.haberes.report.reports.totalesgenerales.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.reports.totalesgenerales.application.service.TotalesGeneralesService;
import um.haberes.report.reports.totalesgenerales.infrastructure.web.dto.TotalesGeneralesResponse;
import um.haberes.report.reports.totalesgenerales.infrastructure.web.dto.TotalesGeneralesResponseMapper;
import um.haberes.report.shared.infrastructure.web.ReportResponseBuilder;

/**
 * Endpoints del namespace nuevo del frontend ({@code /ui/**}): tabla, PDF y XLSX del mismo
 * contenido. Las tres rutas comparten los parámetros y caen bajo la API key del gateway.
 */
@RestController
@RequestMapping("/api/haberes/report/ui/totalesGenerales")
@RequiredArgsConstructor
public class TotalesGeneralesUiController {

    private final TotalesGeneralesService service;
    private final TotalesGeneralesResponseMapper mapper;
    private final ReportResponseBuilder responseBuilder;

    @GetMapping("/data/{anho}/{mes}")
    public TotalesGeneralesResponse data(@PathVariable Integer anho, @PathVariable Integer mes) {
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
