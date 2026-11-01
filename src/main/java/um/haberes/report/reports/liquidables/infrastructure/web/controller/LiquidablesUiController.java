package um.haberes.report.reports.liquidables.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.reports.liquidables.application.service.LiquidablesService;
import um.haberes.report.reports.liquidables.infrastructure.web.dto.LiquidablesResponse;
import um.haberes.report.reports.liquidables.infrastructure.web.dto.LiquidablesResponseMapper;
import um.haberes.report.shared.infrastructure.web.ReportResponseBuilder;

/**
 * Namespace del frontend para la planilla de liquidables. La planilla no lleva período: sale
 * del flag de liquidación vigente, igual que el reporte original.
 */
@RestController
@RequestMapping("/api/haberes/report/ui/liquidables")
@RequiredArgsConstructor
public class LiquidablesUiController {

    private final LiquidablesService service;
    private final LiquidablesResponseMapper mapper;
    private final ReportResponseBuilder responseBuilder;

    @GetMapping("/data")
    public LiquidablesResponse data() {
        return mapper.toResponse(service.content());
    }

    @GetMapping("/pdf")
    public ResponseEntity<Resource> pdf() {
        return responseBuilder.attachment(service.pdf());
    }

    @GetMapping("/xlsx")
    public ResponseEntity<Resource> xlsx() {
        return responseBuilder.attachment(service.xlsx());
    }
}
