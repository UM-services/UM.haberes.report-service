package um.haberes.report.controller;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class CargosClaseController {

    private final CargosClaseService service;

    @GetMapping("/cargos-clase")
    public ResponseEntity<Resource> generateCargosClase() throws FileNotFoundException {
        return Tool.generateFile(service.generate(), "cargos-clase.pdf");
    }
}
