package um.haberes.report.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.service.CursoDocenteService;
import um.haberes.report.util.Tool;

import java.io.FileNotFoundException;

@RestController
@RequestMapping("/api/haberes/report/curso-docente")
public class CursoDocenteController {
    private final CursoDocenteService service;

    public CursoDocenteController(CursoDocenteService service) {
        this.service = service;
    }
    @GetMapping("/pdf/{anho}/{mes}")
    public ResponseEntity<Resource> generateCursoDocente(@PathVariable Integer anho, @PathVariable Integer mes) throws FileNotFoundException {
        return Tool.generateFile(service.generate(anho, mes), "curso-docente.pdf");
    }
}
