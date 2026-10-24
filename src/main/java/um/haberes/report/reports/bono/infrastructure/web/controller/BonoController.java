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

@RestController
@RequestMapping("/api/haberes/report/bono")
@RequiredArgsConstructor
public class BonoController {

    private final BonoService service;
    private final ReportResponseBuilder responseBuilder;

    @GetMapping("/generatePdf/{legajoId}/{anho}/{mes}/{legajoIdSolicitud}/{ipAddress}")
    public ResponseEntity<Resource> generatePdf(@PathVariable Long legajoId,
                                                @PathVariable Integer anho,
                                                @PathVariable Integer mes,
                                                @PathVariable Long legajoIdSolicitud,
                                                @PathVariable String ipAddress) {
        return responseBuilder.attachment(
                service.generatePdf(legajoId, anho, mes, legajoIdSolicitud, ipAddress));
    }

    @GetMapping("/detalleCargos/{legajoId}/{anho}/{mes}/{facultadId}")
    public ResponseEntity<Resource> generateDetalleCargos(@PathVariable Long legajoId,
                                                          @PathVariable Integer anho,
                                                          @PathVariable Integer mes,
                                                          @PathVariable Integer facultadId) {
        return responseBuilder.attachment(
                service.generateDetalleCargosPdf(legajoId, anho, mes, facultadId));
    }

    @GetMapping("/generatePdfDependencia/{anho}/{mes}/{dependenciaId}/{salida}/{legajoIdSolicitud}/{ipAddress}")
    public ResponseEntity<Resource> generatePdfDependencia(@PathVariable Integer anho,
                                                           @PathVariable Integer mes,
                                                           @PathVariable Integer dependenciaId,
                                                           @PathVariable String salida,
                                                           @PathVariable Long legajoIdSolicitud,
                                                           @PathVariable String ipAddress) {
        return responseBuilder.attachment(service.generatePdfDependencia(anho, mes, dependenciaId,
                salida, legajoIdSolicitud, ipAddress));
    }

    @GetMapping("/sendBono/{legajoId}/{anho}/{mes}/{legajoIdSolicitud}/{ipAddress}")
    public String sendBono(@PathVariable Long legajoId, @PathVariable Integer anho,
                           @PathVariable Integer mes, @PathVariable Long legajoIdSolicitud,
                           @PathVariable String ipAddress) {
        return service.sendBono(legajoId, anho, mes, legajoIdSolicitud, ipAddress);
    }

    @GetMapping("/ui/generatePdf/{legajoId}/{anho}/{mes}")
    public ResponseEntity<Resource> generatePdfUi(@PathVariable Long legajoId,
                                                  @PathVariable Integer anho,
                                                  @PathVariable Integer mes) {
        ReportFile file = service.generatePdfUi(legajoId, anho, mes);
        return responseBuilder.inlinePdf(new ReportFile(file.path(),
                service.nombreArchivoBono(legajoId, anho, mes), file.format(), file.size()));
    }

    @GetMapping("/ui/sendBono/{legajoId}/{anho}/{mes}")
    public String sendBonoUi(@PathVariable Long legajoId, @PathVariable Integer anho,
                             @PathVariable Integer mes) {
        return service.sendBonoUi(legajoId, anho, mes);
    }
}
