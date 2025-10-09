package um.haberes.report.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.service.FusionDocenteService;
import um.haberes.report.util.Tool;

import java.io.FileNotFoundException;

@RestController
@RequestMapping("/api/haberes/report/fusionDocente")
@RequiredArgsConstructor
public class FusionDocenteController {

    private final FusionDocenteService service;

    @GetMapping("/fusionDocente/{anho}/{mes}")
    public ResponseEntity<Resource> generateFusionDocente(@PathVariable Integer anho, @PathVariable Integer mes) throws FileNotFoundException {
        return Tool.generateFile(service.generate(anho, mes), "fusion.docente.pdf");
    }

}
