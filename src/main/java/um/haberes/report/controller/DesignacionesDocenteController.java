package um.haberes.report.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.service.DesignacionesDocenteService;
import um.haberes.report.util.Tool;

import java.io.FileNotFoundException;

@RestController
@RequestMapping("/api/haberes/report/designacionesDocente")
@RequiredArgsConstructor
public class DesignacionesDocenteController {

    private final DesignacionesDocenteService service;

    @GetMapping("/pdf/{anho}/{mes}")
    public ResponseEntity<Resource> generateDesignacionesDocente(@PathVariable Integer anho, @PathVariable Integer mes) throws FileNotFoundException {
        return Tool.generateFile(service.generate(anho, mes), "designaciones-docente.pdf");
    }


}
