package um.haberes.report.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.service.ComparacionCodigosService;
import um.haberes.report.util.Tool;

import java.io.FileNotFoundException;

@RestController
@RequestMapping("/api/haberes/report/comparacionCodigos")
public class ComparacionCodigosController {
    private final ComparacionCodigosService service;

    public ComparacionCodigosController(ComparacionCodigosService service) {
        this.service = service;
    }
    @GetMapping("/comparacionCodigos/{anho}/{mes}")
    public ResponseEntity<Resource> generateComparacionCodigos(@PathVariable Integer anho, @PathVariable Integer mes) throws FileNotFoundException {
        return Tool.generateFile(service.generate(anho, mes), "comparacion_codigos.pdf");
    }
}
