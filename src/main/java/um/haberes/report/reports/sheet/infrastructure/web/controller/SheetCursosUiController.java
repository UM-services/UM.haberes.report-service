package um.haberes.report.reports.sheet.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.reports.sheet.application.service.SheetService;
import um.haberes.report.reports.sheet.infrastructure.web.dto.SheetCursosResponse;
import um.haberes.report.reports.sheet.infrastructure.web.dto.SheetCursosResponseMapper;
import um.haberes.report.shared.infrastructure.web.ReportResponseBuilder;

/**
 * Namespace del frontend para la planilla de cursos por período. La ruta legacy
 * {@code /sheet/generatecursos} queda intacta para el cliente de escritorio.
 */
@RestController
@RequestMapping("/api/haberes/report/ui/cursosPeriodo")
@RequiredArgsConstructor
public class SheetCursosUiController {

    private final SheetService service;
    private final SheetCursosResponseMapper mapper;
    private final ReportResponseBuilder responseBuilder;

    @GetMapping("/data/{anho}/{mes}")
    public SheetCursosResponse data(@PathVariable Integer anho, @PathVariable Integer mes) {
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
