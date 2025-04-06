package um.haberes.report.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.service.CargosClaseService;
import um.haberes.report.util.Tool;

import java.io.FileNotFoundException;

@RestController
@RequestMapping("/api/haberes/report/cargos")
public class CargosClaseController {

    private final CargosClaseService service;

    public CargosClaseController(CargosClaseService service) {
        this.service = service;
    }

    @GetMapping("/cargos-clase")
    public ResponseEntity<Resource> generateCargosClase() throws FileNotFoundException {
        return Tool.generateFile(service.generate(), "cargos-clase.pdf");
    }
}
