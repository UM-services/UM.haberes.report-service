package um.haberes.report.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.service.DocentesSedeService;
import um.haberes.report.util.Tool;

import java.io.FileNotFoundException;

@RestController
@RequestMapping("/api/haberes/report/docentes")
public class DocentesController {

    private final DocentesSedeService docentesSedeService;

    public DocentesController(DocentesSedeService docentesSedeService) {
        this.docentesSedeService = docentesSedeService;
    }

    @GetMapping("/docentesSede/{facultadId}/{geograficaId}/{anho}/{mes}")
    public ResponseEntity<Resource> generateDocentesSede(@PathVariable Integer facultadId, @PathVariable Integer geograficaId, @PathVariable Integer anho, @PathVariable Integer mes) throws FileNotFoundException {
        return Tool.generateFile(docentesSedeService.generate(facultadId, geograficaId, anho, mes), "docentesSede.pdf");
    }

}
