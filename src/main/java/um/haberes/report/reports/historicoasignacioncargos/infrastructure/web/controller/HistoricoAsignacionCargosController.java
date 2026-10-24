package um.haberes.report.reports.historicoasignacioncargos.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.reports.historicoasignacioncargos.application.service.HistoricoAsignacionCargosService;
import um.haberes.report.shared.infrastructure.web.ReportResponseBuilder;

@RestController
@RequestMapping("/api/haberes/report/historico")
@RequiredArgsConstructor
public class HistoricoAsignacionCargosController {

    private final HistoricoAsignacionCargosService service;
    private final ReportResponseBuilder responseBuilder;

    @GetMapping("historicoAsignacionDeCargos/{categoriaId}")
    public ResponseEntity<Resource> generateHistoricoAsignacionCargos(
            @PathVariable Integer categoriaId) {
        return responseBuilder.attachment(service.generate(categoriaId));
    }
}
