package um.haberes.report.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.haberes.report.service.TotalesGeneralesService;
import um.haberes.report.util.Tool;

import java.io.FileNotFoundException;

@RestController
@RequestMapping("/api/haberes/report/totalesGenerales")
public class TotalesGeneralesController {

    private final TotalesGeneralesService service;

    public TotalesGeneralesController(TotalesGeneralesService service) {
        this.service = service;
    }


    @GetMapping("/totalesGenerales/{anho}/{mes}")
    public ResponseEntity<Resource> generateTotalesGenerales(@PathVariable Integer anho, @PathVariable Integer mes) throws FileNotFoundException {
        return Tool.generateFile(service.generate(anho, mes), "totales_generales.pdf");
    }

}
