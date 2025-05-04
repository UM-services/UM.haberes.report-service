package um.haberes.report.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.service.NovedadesDocentesPendientesService;
import um.haberes.report.util.Tool;

import java.io.FileNotFoundException;

@RestController
@RequestMapping("/api/haberes/report/novedadesDocentePendientes")
public class NovedadesDocentesPendientesController {

    private final NovedadesDocentesPendientesService service;

    public NovedadesDocentesPendientesController(NovedadesDocentesPendientesService service) {
        this.service = service;
    }

    @GetMapping("/novedadesDocentesPendientes/{anho}/{mes}/{autorizado}/{rechazado}")
    public ResponseEntity<Resource> generateNovedadesDocentesPendientes(@PathVariable Integer anho, @PathVariable Integer mes, @PathVariable Integer autorizado, @PathVariable Integer rechazado) throws FileNotFoundException {
        return Tool.generateFile(service.generate(anho, mes, autorizado, rechazado), "novedadesDocentesPendientes.pdf");
    }

}
