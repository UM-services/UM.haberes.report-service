package um.haberes.report.reports.historicoasignacioncargos.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.reports.historicoasignacioncargos.application.service.HistoricoAsignacionCargosService;
import um.haberes.report.reports.historicoasignacioncargos.infrastructure.web.dto.HistoricoAsignacionCargosResponse;
import um.haberes.report.reports.historicoasignacioncargos.infrastructure.web.dto.HistoricoAsignacionCargosResponseMapper;
import um.haberes.report.shared.infrastructure.web.ReportResponseBuilder;

/**
 * Namespace del frontend para el histórico de asignación de cargos. Sólo hay `data` (con el
 * aviso de cuerpo no implementado) y PDF: no se ofrece tabla ni XLSX.
 */
@RestController
@RequestMapping("/api/haberes/report/ui/historicoCargos")
@RequiredArgsConstructor
public class HistoricoAsignacionCargosUiController {

    private final HistoricoAsignacionCargosService service;
    private final HistoricoAsignacionCargosResponseMapper mapper;
    private final ReportResponseBuilder responseBuilder;

    @GetMapping("/data/{categoriaId}")
    public HistoricoAsignacionCargosResponse data(@PathVariable Integer categoriaId) {
        return mapper.toResponse(service.content(categoriaId));
    }

    @GetMapping("/pdf/{categoriaId}")
    public ResponseEntity<Resource> pdf(@PathVariable Integer categoriaId) {
        return responseBuilder.attachment(service.generate(categoriaId));
    }
}
