package um.haberes.report.reports.cargosperiodo.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.reports.cargosperiodo.application.service.CargosPeriodoService;
import um.haberes.report.reports.cargosperiodo.infrastructure.web.dto.CargosPeriodoResponse;
import um.haberes.report.reports.cargosperiodo.infrastructure.web.dto.CargosPeriodoResponseMapper;
import um.haberes.report.shared.infrastructure.web.ReportResponseBuilder;

/** Namespace del frontend: tabla, PDF y XLSX del mismo contenido. */
@RestController
@RequestMapping("/api/haberes/report/ui/cargosPeriodo")
@RequiredArgsConstructor
public class CargosPeriodoUiController {

    private final CargosPeriodoService service;
    private final CargosPeriodoResponseMapper mapper;
    private final ReportResponseBuilder responseBuilder;

    @GetMapping("/data/{anho}/{mes}")
    public CargosPeriodoResponse data(@PathVariable Integer anho, @PathVariable Integer mes) {
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
