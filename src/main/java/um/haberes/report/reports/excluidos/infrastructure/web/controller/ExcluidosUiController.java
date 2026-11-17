package um.haberes.report.reports.excluidos.infrastructure.web.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import um.haberes.report.reports.excluidos.application.service.ExcluidosService;
import um.haberes.report.reports.excluidos.domain.model.ExcluidosContent;
import um.haberes.report.shared.infrastructure.web.ReportResponseBuilder;

@RestController
@RequestMapping("/api/haberes/report/ui/excluidos")
@RequiredArgsConstructor
public class ExcluidosUiController {

    private final ExcluidosService service;
    private final ReportResponseBuilder responseBuilder;

    @GetMapping("/data/{anho}/{mes}")
    public ExcluidosContent data(@PathVariable Integer anho, @PathVariable Integer mes) {
        return service.content(anho, mes);
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
