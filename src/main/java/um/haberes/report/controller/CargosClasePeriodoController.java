package um.haberes.report.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.service.CargosClasePeriodoService;
import um.haberes.report.util.Tool;

import java.io.FileNotFoundException;

@RestController
@RequestMapping("/api/haberes/report/cargosClasePeriodo")
public class CargosClasePeriodoController {

    private final CargosClasePeriodoService service;

    public CargosClasePeriodoController(CargosClasePeriodoService service) {
        this.service = service;
    }

    @GetMapping("/pdf/{facultadId}/{anho}/{mes}")
    public ResponseEntity<Resource> generateCargosClasePeriodo(@PathVariable Integer facultadId, @PathVariable Integer anho,@PathVariable Integer mes) throws FileNotFoundException {
        return Tool.generateFile(service.generate(facultadId, anho, mes), "cargos-clase-periodo.pdf");
    }

}
