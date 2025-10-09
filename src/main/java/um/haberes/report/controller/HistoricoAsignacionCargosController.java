package um.haberes.report.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.service.HistoricoAsignacionCargosService;
import um.haberes.report.util.Tool;

import java.io.FileNotFoundException;

@RestController
@RequestMapping("/api/haberes/report/historico")
@RequiredArgsConstructor
public class HistoricoAsignacionCargosController {

    private final HistoricoAsignacionCargosService service;

    @GetMapping("historicoAsignacionDeCargos/{categoriaId}")
    public ResponseEntity<Resource> generateHistoricoAsignacionCargos(@PathVariable Integer categoriaId) throws FileNotFoundException {
        return Tool.generateFile(service.generate(categoriaId), "historicoAsignacionDeCargos.pdf");
    }
}
