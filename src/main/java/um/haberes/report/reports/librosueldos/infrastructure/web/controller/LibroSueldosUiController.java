package um.haberes.report.reports.librosueldos.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.reports.librosueldos.application.service.LibroSueldosService;
import um.haberes.report.reports.librosueldos.infrastructure.web.dto.LibroSueldosResponse;
import um.haberes.report.reports.librosueldos.infrastructure.web.dto.LibroSueldosResponseMapper;
import um.haberes.report.shared.infrastructure.web.ReportResponseBuilder;

import java.util.List;

/**
 * Namespace del frontend: tabla, PDF propio y XLSX. El ZIP del Libro de Sueldos Digital se pide
 * con POST porque el cuerpo lleva los legajos que se están mostrando.
 */
@RestController
@RequestMapping("/api/haberes/report/ui/libroSueldos")
@RequiredArgsConstructor
public class LibroSueldosUiController {

    private final LibroSueldosService service;
    private final LibroSueldosResponseMapper mapper;
    private final ReportResponseBuilder responseBuilder;

    @GetMapping("/data/{anho}/{mes}")
    public LibroSueldosResponse data(@PathVariable Integer anho, @PathVariable Integer mes) {
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

    @PostMapping("/lsd/{anho}/{mes}")
    public ResponseEntity<Resource> lsd(@PathVariable Integer anho, @PathVariable Integer mes,
                                        @RequestBody List<Long> legajoIds) {
        return responseBuilder.attachment(service.lsd(anho, mes, legajoIds));
    }
}
