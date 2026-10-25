package um.haberes.report.reports.bono.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.reports.bono.application.service.BonoService;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.infrastructure.web.ReportResponseBuilder;

/**
 * Namespace del frontend para el bono individual. Vive bajo {@code /api/haberes/report/ui/**}
 * para quedar detrás de la API key que inyecta el gateway; las rutas legacy con IP siguen en
 * {@link BonoController} para el cliente de escritorio.
 */
@RestController
@RequestMapping("/api/haberes/report/ui/bono")
@RequiredArgsConstructor
public class BonoUiController {

    private final BonoService service;
    private final ReportResponseBuilder responseBuilder;

    @GetMapping("/generatePdf/{legajoId}/{anho}/{mes}")
    public ResponseEntity<Resource> generatePdf(@PathVariable Long legajoId,
                                                @PathVariable Integer anho,
                                                @PathVariable Integer mes) {
        ReportFile file = service.generatePdfUi(legajoId, anho, mes);
        return responseBuilder.inlinePdf(new ReportFile(file.path(),
                service.nombreArchivoBono(legajoId, anho, mes), file.format(), file.size()));
    }

    @GetMapping("/sendBono/{legajoId}/{anho}/{mes}")
    public String sendBono(@PathVariable Long legajoId, @PathVariable Integer anho,
                           @PathVariable Integer mes) {
        return service.sendBonoUi(legajoId, anho, mes);
    }
}
