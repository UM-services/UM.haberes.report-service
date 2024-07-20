package um.haberes.report.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.service.AnotadorAutorizadosRechazadosService;
import um.haberes.report.service.AnotadorPendientesService;
import um.haberes.report.util.Tool;

import java.io.FileNotFoundException;

@RestController
@RequestMapping("/api/haberes/report/anotador")
public class AnotadorController {

    private final AnotadorPendientesService anotadorPendientesService;
    private final AnotadorAutorizadosRechazadosService anotadorAutorizadosRechazadosService;

    public AnotadorController(AnotadorPendientesService anotadorPendientesService, AnotadorAutorizadosRechazadosService anotadorAutorizadosRechazadosService) {
        this.anotadorPendientesService = anotadorPendientesService;
        this.anotadorAutorizadosRechazadosService = anotadorAutorizadosRechazadosService;
    }

    @GetMapping("/anotadorPendientes/{facultadId}/{anho}/{mes}")
    public ResponseEntity<Resource> generateAnotadorPendientes(@PathVariable Integer facultadId, @PathVariable Integer anho, @PathVariable Integer mes) throws FileNotFoundException {
        return Tool.generateFile(anotadorPendientesService.generate(facultadId, anho, mes), "anotadorPendientes.pdf");
    }

    @GetMapping("/anotadorAutorizadosRechazados/{facultadId}/{anho}/{mes}")
    public ResponseEntity<Resource> generateAnotadorAutorizadosRechazados(@PathVariable Integer facultadId, @PathVariable Integer anho, @PathVariable Integer mes) throws FileNotFoundException {
        return Tool.generateFile(anotadorAutorizadosRechazadosService.generate(facultadId, anho, mes), "anotadorAutorizadosRechazados.pdf");
    }

}
