package um.haberes.report.reports.bono.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import um.haberes.report.reports.bono.application.service.BonoService;
import um.haberes.report.reports.bono.infrastructure.job.BonoPdfJob;
import um.haberes.report.reports.bono.infrastructure.job.BonoPdfJobService;
import um.haberes.report.reports.bono.infrastructure.web.dto.BonoDependenciaPdfJobResponse;
import um.haberes.report.reports.bono.infrastructure.web.dto.BonosDependenciaResponse;
import um.haberes.report.reports.bono.infrastructure.web.dto.BonosDependenciaResponseMapper;
import um.haberes.report.shared.infrastructure.web.ReportResponseBuilder;

/**
 * Namespace del frontend para el bono por dependencia: grilla, PDF consolidado y envío unitario.
 *
 * <p>La preparación de la actividad y la auditoría de la impresión se resuelven en el core
 * ({@code POST /bono/dependencia/.../print-prepare}), que es quien conoce la IP del cliente;
 * estas rutas no reciben ni registran IP.
 *
 * <p>El PDF se genera en segundo plano ({@code POST /pdf/...} + polling de
 * {@code GET /pdf/job/{jobId}}) para no quedar atado al timeout del proxy; la descarga sigue
 * disponible en {@code GET /pdf/{anho}/{mes}/{dependenciaId}/{salida}} para los consumidores
 * existentes.
 */
@RestController
@RequestMapping("/api/haberes/report/ui/bonoDependencia")
@RequiredArgsConstructor
public class BonosDependenciaUiController {

    private final BonoService service;
    private final BonoPdfJobService jobService;
    private final BonosDependenciaResponseMapper mapper;
    private final ReportResponseBuilder responseBuilder;

    @GetMapping("/data/{anho}/{mes}/{dependenciaId}/{salida}")
    public BonosDependenciaResponse data(@PathVariable Integer anho, @PathVariable Integer mes,
                                         @PathVariable Integer dependenciaId,
                                         @PathVariable String salida) {
        return mapper.toResponse(service.contenidoDependencia(anho, mes, dependenciaId, salida));
    }

    @GetMapping("/pdf/{anho}/{mes}/{dependenciaId}/{salida}")
    public ResponseEntity<Resource> pdf(@PathVariable Integer anho, @PathVariable Integer mes,
                                        @PathVariable Integer dependenciaId,
                                        @PathVariable String salida) {
        return responseBuilder.attachment(
                service.generatePdfDependenciaUi(anho, mes, dependenciaId, salida));
    }

    /** Inicia la generación del PDF en segundo plano y devuelve el job para consultar el avance. */
    @PostMapping("/pdf/{anho}/{mes}/{dependenciaId}/{salida}")
    public ResponseEntity<BonoDependenciaPdfJobResponse> iniciarPdf(@PathVariable Integer anho,
                                                                    @PathVariable Integer mes,
                                                                    @PathVariable Integer dependenciaId,
                                                                    @PathVariable String salida) {
        return ResponseEntity.accepted()
                .body(mapper.toResponse(jobService.iniciar(anho, mes, dependenciaId, salida)));
    }

    @GetMapping("/pdf/job/{jobId}")
    public BonoDependenciaPdfJobResponse estadoPdf(@PathVariable String jobId) {
        return mapper.toResponse(jobService.obtener(jobId));
    }

    @GetMapping("/pdf/job/{jobId}/file")
    public ResponseEntity<Resource> archivoPdf(@PathVariable String jobId) {
        BonoPdfJob job = jobService.obtener(jobId);
        if (job.estado() != BonoPdfJob.Estado.LISTO || job.archivo() == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El PDF todavía no está disponible");
        }
        return responseBuilder.attachment(job.archivo());
    }

    @GetMapping("/sendBono/{legajoId}/{anho}/{mes}")
    public String sendBono(@PathVariable Long legajoId, @PathVariable Integer anho,
                           @PathVariable Integer mes) {
        return service.sendBonoUi(legajoId, anho, mes);
    }
}
